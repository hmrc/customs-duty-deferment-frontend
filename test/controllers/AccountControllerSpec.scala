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

import connectors.{FinancialsApiConnector, SessionCacheConnector}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.DocumentService
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase

import scala.concurrent.Future

class AccountControllerSpec extends SpecBase {

  "showAccountDetails" should {
    "redirect to the financials homepage if there is no session available" in {
      val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      val app: Application = application().overrides(
        inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
      ).build()

      running(app) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "http://localhost:9876/customs/payment-records"
      }
    }

    "display the account page on a successful response" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockApiConnector.deleteNotification(any, any)(any))
        .thenReturn(Future.successful(true))

      running(app) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe OK
      }
    }
  }

  "statementsUnavailablePage" should {
    "return unauthorized page when no account link found" in {
      val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      val app: Application = application().overrides(
        inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
      ).build()

      running(app) {
        val request = FakeRequest(GET, routes.AccountController.statementsUnavailablePage("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe UNAUTHORIZED

      }
    }

    "return the accounts unavailable page when an account link found" in {
      val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      val app: Application = application().overrides(
        inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
      ).build()

      running(app) {
        val request = FakeRequest(GET, routes.AccountController.statementsUnavailablePage("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe OK
      }
    }
  }

  trait Setup {
    val mockApiConnector: FinancialsApiConnector = mock[FinancialsApiConnector]
    val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]
    val mockDocumentService: DocumentService = mock[DocumentService]

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = application().overrides(
      inject.bind[FinancialsApiConnector].toInstance(mockApiConnector),
      inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
    ).build()
  }
}
