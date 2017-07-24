import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit.{MILLISECONDS, SECONDS}

import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.{MetricFilter, SharedMetricRegistries}
import com.google.inject.AbstractModule
import org.slf4j.MDC
import play.api.{Configuration, Environment, Logger, Mode}
import uk.gov.hmrc.play.http.HeaderCarrier

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  /**
    * Configuration for enabling and publishing to graphite for metrics data
    */
  val metricsPluginEnabled: Boolean = configuration.getBoolean("metrics.enabled").getOrElse(false)
  val graphitePublisherEnabled: Boolean = configuration.getBoolean("microservice.metrics.graphite.enabled").getOrElse(false)

  val graphiteEnabled: Boolean = metricsPluginEnabled && graphitePublisherEnabled

  val registryName: String = configuration.getString("metrics.name").getOrElse("default")

  private def startGraphite() {
    Logger.info("Graphite metrics enabled, starting the reporter")

    val graphite = new Graphite(new InetSocketAddress(
      configuration.getString("graphite.host").getOrElse("graphite"),
      configuration.getInt("graphite.port").getOrElse(2003)))

    val prefix = configuration.getString("graphite.prefix").getOrElse(s"tax.${configuration.getString("appName")}")

    val reporter = GraphiteReporter.forRegistry(
      SharedMetricRegistries.getOrCreate(registryName))
      .prefixedWith(s"$prefix.${java.net.InetAddress.getLocalHost.getHostName}")
      .convertRatesTo(SECONDS)
      .convertDurationsTo(MILLISECONDS)
      .filter(MetricFilter.ALL)
      .build(graphite)

    reporter.start(configuration.getLong("graphite.interval").getOrElse(10L), SECONDS)
  }

  if (graphiteEnabled) startGraphite()

  /**
    * Checks whether an API needs to be sent to the API publisher services.
    * This only applies to third party APIs and no other services.
    */
  val registrationEnabled: Boolean = configuration.getBoolean(s"$environment.microservice.services.service-locator.enabled").getOrElse(true)

  def configure(): Unit = {
    lazy val appName = configuration.getString("appName").get
    lazy val loggerDateFormat: Option[String] = configuration.getString("logger.json.dateformat")

    implicit val hc: HeaderCarrier = HeaderCarrier()

    if (environment.mode != Mode.Test && registrationEnabled) {
      //TODO - Register api with service locator using register method
    }

    Logger.info(s"Starting microservice : $appName : in mode : ${environment.mode}")
    MDC.put("appName", appName)
    loggerDateFormat.foreach(str => MDC.put("logger.json.dateformat", str))

  }
}

