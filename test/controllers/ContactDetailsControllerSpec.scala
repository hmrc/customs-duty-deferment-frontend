/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import connectors.SessionCacheConnector
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.ContactDetailsService
import util.SpecBase

import scala.concurrent.Future

class ContactDetailsControllerSpec extends SpecBase {

  "showContactDetails" should {
    "return redirect to financials homepage if account link not available" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      running(app) {
        val request = FakeRequest(GET, routes.ContactDetailsController.showContactDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "http://localhost:9876/customs/payment-records"
      }
    }

    "return NOT_FOUND if account status is not available" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink.copy(accountStatusId = None))))

      running(app) {
        val request = FakeRequest(GET, routes.ContactDetailsController.showContactDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe NOT_FOUND
      }

    }

    "return INTERNAL_SERVER_ERROR if an issue with encryption occurs" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))
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
        .thenReturn(Future.successful(Some(accountLink)))
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
