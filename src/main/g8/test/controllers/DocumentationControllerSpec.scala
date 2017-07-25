package controllers

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class DocumentationControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  val mockDocumentation = new DocumentationController()

  "Documentation" should {
    "return OK status for definition endpoint" in {
      val response = mockDocumentation.definition()(FakeRequest())
      status(response) mustBe OK
    }

    "return OK status for raml endpoint" in {
      val response = mockDocumentation.raml("1.0", "application.raml")(FakeRequest())
      status(response) mustBe OK
    }
  }
}
