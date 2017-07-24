package controllers

import javax.inject._

import models._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

@Singleton
class HelloWorldController @Inject() extends Controller with HeaderValidator {

  def helloWorld(): Action[AnyContent] = Action.async {
    implicit request =>
      Future successful Ok(Json.toJson(Model("world")))
  }
}
