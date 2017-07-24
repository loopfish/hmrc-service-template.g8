import play.api.libs.json._

package object models {
  implicit val errorFormat: Format[Error] = Json.format[Error]
  implicit val modelFormat: Format[Model] = Json.format[Model]
  implicit val registrationFormat: Format[Registration] = Json.format[Registration]
}
