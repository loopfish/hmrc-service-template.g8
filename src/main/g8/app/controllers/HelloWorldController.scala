package controllers

import javax.inject._

import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

@Singleton
class HelloWorldController @Inject() extends Controller with HeaderValidator {

  def helloWorld(): Action[AnyContent] = validateAccept(acceptHeaderValidationRules).async {
    implicit request =>
      Future successful Ok
  }
}
