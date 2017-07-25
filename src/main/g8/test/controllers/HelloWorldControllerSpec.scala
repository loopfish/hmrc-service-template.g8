package controllers

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HeaderCarrier

class HelloWorldControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  val mockHelloWorldController = new HelloWorldController()

  val acceptedHeaders: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+json"

  implicit val hc = new HeaderCarrier

  "HelloWorldController" should {
    "return not acceptable when provided with no accept header" in {
      val response = mockHelloWorldController.helloWorld()(FakeRequest())

      status(response) mustBe NOT_ACCEPTABLE
    }
    "return Status: OK Body: hello: world" in {
      val response = mockHelloWorldController.helloWorld()(FakeRequest().withHeaders(acceptedHeaders))

      status(response) mustBe OK
      contentType(response).get mustBe JSON
      contentAsJson(response) mustBe Json.parse("""{"hello": "world"}""")
    }
  }
}


