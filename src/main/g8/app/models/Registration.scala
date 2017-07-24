package models

case class Registration(serviceName: String, serviceUrl: String, metadata: Option[Map[String, String]] = None)
