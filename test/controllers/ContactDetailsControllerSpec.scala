package controllers

import connectors.SessionCacheConnector
import models.{AccountLink, AccountStatusOpen, DirectDebitMandateCancelled}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.ContactDetailsService
import util.SpecBase

import scala.concurrent.Future

class ContactDetailsControllerSpec extends SpecBase {

  "showContactDetails" should {
    "return NOT_FOUND if account link not available" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      running(app) {
        val request = FakeRequest(GET, routes.ContactDetailsController.showContactDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe NOT_FOUND
      }
    }

    "return NOT_FOUND if account status is not available" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(AccountLink("accountNumber", "linkId", AccountStatusOpen, None))))

      running(app) {
        val request = FakeRequest(GET, routes.ContactDetailsController.showContactDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe NOT_FOUND
      }

    }

    "return INTERNAL_SERVER_ERROR if an issue with encryption occurs" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(AccountLink("accountNumber", "linkId", AccountStatusOpen, Some(DirectDebitMandateCancelled)))))
      when(mockContactDetailsService.getEncyptedDanWithStatus(any, any))
        .thenReturn(Future.failed(new RuntimeException("Unknown error")))

      running(app) {
        val request = FakeRequest(GET, routes.ContactDetailsController.showContactDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "redirect to contact details on a successful request" in new Setup{
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(AccountLink("accountNumber", "linkId", AccountStatusOpen, Some(DirectDebitMandateCancelled)))))
      when(mockContactDetailsService.getEncyptedDanWithStatus(any, any))
        .thenReturn(Future.successful("encryptedParams"))

      running(app) {
        val request = FakeRequest(GET, routes.ContactDetailsController.showContactDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "encryptedParams"
      }
    }
  }

  trait Setup {
    val mockContactDetailsService: ContactDetailsService = mock[ContactDetailsService]
    val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

    val app: Application = application().overrides(
      inject.bind[ContactDetailsService].toInstance(mockContactDetailsService),
      inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
    ).build()
  }
}
