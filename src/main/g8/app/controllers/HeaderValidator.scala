package controllers

import models.Error
import play.api.libs.json.Json.{toJson => js}
import play.api.mvc.{ActionBuilder, Request, Result, Results}

import scala.concurrent.Future
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

trait HeaderValidator extends Results {

  val validateVersion: String => Boolean = _ == "1.0"

  val validateContentType: String => Boolean = _ == "json"

  val matchHeader: String => Option[Match] = new Regex( """^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$""", "version", "contenttype") findFirstMatchIn _

  val acceptHeaderValidationRules: Option[String] => Boolean =
    _ flatMap (a => matchHeader(a) map (res => validateContentType(res.group("contenttype")) && validateVersion(res.group("version")))) getOrElse false


  def validateAccept(rules: Option[String] => Boolean) = new ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      if (rules(request.headers.get("Accept"))) block(request)
      else Future.successful(NotAcceptable(js(Error("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid"))))
    }
  }
}
