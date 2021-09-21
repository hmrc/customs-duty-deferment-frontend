package controllers

import connectors.SessionCacheConnector
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import util.SpecBase

import scala.concurrent.Future

class LogoutControllerSpec extends SpecBase {

  "logout" should {
    "redirect to the logout route with a continueUrl to feedback survey" in new Setup {
      running(app) {
        val request = FakeRequest(GET, routes.LogoutController.logout().url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "http://localhost:9553/bas-gateway/sign-out-without-state?continue=https%3A%2F%2Fwww.development.tax.service.gov.uk%2Ffeedback%2FCDS-FIN"
      }
    }
  }

  "logoutNoSurvey" should {
    "redirect to the logout route without a continueUrl to the feedback survey" in new Setup {
      running(app) {
        val request = FakeRequest(GET, routes.LogoutController.logoutNoSurvey().url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http%3A%2F%2Flocalhost%3A9876%2Fcustoms%2Fpayment-records"
      }
    }
  }

  trait Setup {
    val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

    when(mockSessionCacheConnector.removeSession(any)(any))
      .thenReturn(Future.successful(true))

    val app: Application = application().overrides(
      inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
    ).build()
  }

}
