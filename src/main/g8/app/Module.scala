import java.net.{ConnectException, InetSocketAddress}
import java.util.concurrent.TimeUnit.{MILLISECONDS, SECONDS}
import javax.inject.{Inject, Singleton}

import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.{MetricFilter, SharedMetricRegistries}
import com.google.inject.AbstractModule
import models._
import org.slf4j.MDC
import play.api._
import play.api.http.Status._
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import uk.gov.hmrc.play.config.inject.DefaultServicesConfig

import scala.concurrent.{ExecutionContext, Future}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {
    lazy val appName = configuration.getString("appName").get
    lazy val loggerDateFormat: Option[String] = configuration.getString("logger.json.dateformat")

    Logger.info(s"Starting microservice : $appName : in mode : ${environment.mode}")
    MDC.put("appName", appName)
    loggerDateFormat.foreach(str => MDC.put("logger.json.dateformat", str))

    bind(classOf[ServiceLocator]).asEagerSingleton()

    bind(classOf[GraphiteStartUp]).asEagerSingleton()
  }
}

@Singleton
class ServiceLocator @Inject()(configuration: Configuration, serviceConfig: DefaultServicesConfig,
                               ws: WSClient, env: Environment, implicit val ec: ExecutionContext) {

  private val appName = configuration.getString("appName").get
  private val appUrl = configuration.getString("appUrl").get
  private val serviceUrl = s"${serviceConfig.baseUrl("service-locator")}/registration"

  val metadata: Option[Map[String, String]] = Some(Map("third-party-api" -> "true"))

  def register(): Future[Unit] = {
    val registration = Json.toJson(Registration(appName, appUrl, metadata))
    ws.url(serviceUrl).withHeaders("Content-Type" -> "application/json").post(registration) map {
      result =>
        result.status match {
          //Expected response from service locator service
          case NO_CONTENT => Logger.info("Service is registered on the service locator")
          case _ => Logger.error(s"Service could not register on the service locator: ${result.body}")
        }
    } recover {
      //Occurs if the required services aren't started up
      case r: ConnectException => Logger.error(s"Service could not register on the service locator: ${r.getMessage}")
    }
  }

  val registrationEnabled: Boolean = configuration.getBoolean(s"microservice.services.service-locator.enabled").getOrElse(true)

  if (registrationEnabled && env.mode != Mode.Test) register()
}

@Singleton
class GraphiteStartUp @Inject()(configuration: Configuration,
                                lifecycle: ApplicationLifecycle,
                                implicit val ec: ExecutionContext) {

  val metricsPluginEnabled: Boolean = configuration.getBoolean("metrics.enabled").getOrElse(false)

  val graphitePublisherEnabled: Boolean = configuration.getBoolean("microservice.metrics.graphite.enabled").getOrElse(false)

  val graphiteEnabled: Boolean = metricsPluginEnabled && graphitePublisherEnabled

  val registryName: String = configuration.getString("metrics.name").getOrElse("default")

  val graphite = new Graphite(new InetSocketAddress(
    configuration.getString("graphite.host").getOrElse("graphite"),
    configuration.getInt("graphite.port").getOrElse(2003)))

  val prefix: String = configuration.getString("graphite.prefix").
    getOrElse(s"tax.${configuration.getString("appName")}")

  val reporter: GraphiteReporter = GraphiteReporter.forRegistry(
    SharedMetricRegistries.getOrCreate(registryName))
    .prefixedWith(s"$prefix.${java.net.InetAddress.getLocalHost.getHostName}")
    .convertRatesTo(SECONDS)
    .convertDurationsTo(MILLISECONDS)
    .filter(MetricFilter.ALL)
    .build(graphite)

  private def startGraphite() {
    Logger.info("Graphite metrics enabled, starting the reporter")
    reporter.start(configuration.getLong("graphite.interval").getOrElse(10L), SECONDS)
  }

  if (graphiteEnabled) startGraphite()
  lifecycle.addStopHook { () =>
    Future successful reporter.stop()
  }
}
