/*
 * Copyright 2023 HM Revenue & Customs
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

import config.AppConfig
import connectors.{CustomsFinancialsApiConnector, SessionCacheConnector}
import navigation.Navigator
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.DocumentService
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase
import viewmodels.DutyDefermentAccount
import views.html.duty_deferment_account.{duty_deferment_account, duty_deferment_statements_not_available}

import scala.concurrent.Future

class AccountControllerSpec extends SpecBase {

  "showAccountDetails" should {
    "return unauthorized if no session id present" in {
      val app: Application = application().build()

      running(app) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)

        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
      }
    }

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

    "redirect to statements unavailable page if a failure occurs receiving historic statements" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDocumentService.getDutyDefermentStatements(any, any)(any))
        .thenReturn(Future.failed(new RuntimeException("Unknown failure")))

      when(mockApiConnector.deleteNotification(any, any)(any))
        .thenReturn(Future.successful(true))

      running(app) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")
        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.AccountController.statementsUnavailablePage("someLink").url
      }
    }

    "display the account page on a successful response" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDocumentService.getDutyDefermentStatements(any, any)(any))
        .thenReturn(Future.successful(dutyDefermentStatementsForEori))

      when(mockApiConnector.deleteNotification(any, any)(any))
        .thenReturn(Future.successful(true))

      val view: duty_deferment_account = app.injector.instanceOf[duty_deferment_account]
      val request: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, routes.AccountController.showAccountDetails(linkId).url).withHeaders(
          "X-Session-Id" -> "someSessionId")

      val model: DutyDefermentAccount = DutyDefermentAccount(
        "accountNumber",
        Seq(dutyDefermentStatementsForEori),
        "linkId",
        isNiAccount = false)

      val messages: Messages = messagesApi.preferred(request)
      running(app) {

        val result = route(app, request).value
        status(result) mustBe OK
        contentAsString(result) mustBe view(model, Some(serviceUnavailableUrl))(request, messages, appConfig).toString
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

        val view = app.injector.instanceOf[duty_deferment_statements_not_available]
        val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
        val navigator = app.injector.instanceOf[Navigator]
        val linkId = "someLink"
        val serviceUnavailableUrl: String =
          routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementNAPageId, linkId).url

        running(app) {
          val request = FakeRequest(GET, routes.AccountController.statementsUnavailablePage(linkId).url)
            .withHeaders("X-Session-Id" -> "someSessionId")
          val result = route(app, request).value
          status(result) mustBe OK
          contentAsString(result) mustBe
            view("accountNumber", linkId, Some(serviceUnavailableUrl))(request, messages(app), appConfig).toString()
        }
      }
    }

    trait Setup {
      val mockApiConnector: CustomsFinancialsApiConnector = mock[CustomsFinancialsApiConnector]
      val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]
      val mockDocumentService: DocumentService = mock[DocumentService]
      val navigator = new Navigator()

      val linkId = "someLink"
      val serviceUnavailableUrl: String =
        routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementPageId, linkId).url

      implicit val hc: HeaderCarrier = HeaderCarrier()

      val app: Application = application().overrides(
        inject.bind[CustomsFinancialsApiConnector].toInstance(mockApiConnector),
        inject.bind[DocumentService].toInstance(mockDocumentService),
        inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector),
        inject.bind[Navigator].toInstance(navigator)
      ).build()

      val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
      val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    }
  }
}
