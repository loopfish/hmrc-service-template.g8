package connectors

import javax.inject.{Inject, Singleton}

import models.Registration
import play.api.{Configuration, Logger}
import uk.gov.hmrc.play.config.inject.DefaultServicesConfig
import uk.gov.hmrc.play.http.HeaderCarrier
import wiring.WSVerbs

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ServiceLocatorConnector @Inject()(configuration: Configuration, http: WSVerbs, serviceConfig: DefaultServicesConfig) {
  private val appName = configuration.getString("appName").get
  private val appUrl = configuration.getString("appUrl").get
  private val serviceUrl = serviceConfig.baseUrl("service-locator")

  val metadata: Option[Map[String, String]] = Some(Map("third-party-api" -> "true"))

  def register(implicit hc: HeaderCarrier): Future[Boolean] = {
    val registration = Registration(appName, appUrl, metadata)
    http.POST(s"$serviceUrl/registration", registration, Seq("Content-Type" -> "application/json")) map {
      _ =>
        Logger.info("Service is registered on the service locator")
        true
    } recover {
      case e: Throwable =>
        Logger.error(s"Service could not register on the service locator", e)
        false
    }
  }
}



