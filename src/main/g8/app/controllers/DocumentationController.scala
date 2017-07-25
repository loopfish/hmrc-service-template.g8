package controllers

import javax.inject.Singleton

import play.api.http.LazyHttpErrorHandler
import play.api.mvc.{Action, AnyContent, Controller}

@Singleton
class DocumentationController extends AssetsBuilder(LazyHttpErrorHandler) with Controller {

  def definition: Action[AnyContent] = at("/public/api", "definition.json")

  def raml(version: String, file: String): Action[AnyContent] = at(s"/public/api/conf/$version", file)
}

