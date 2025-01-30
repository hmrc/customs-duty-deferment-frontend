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

import connectors.{CustomsFinancialsApiConnector, SessionCacheConnector}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.{Application, inject}
import services.DocumentService
import util.SpecBase
import config.AppConfig
import viewmodels.DutyDefermentAccountViewModel
import views.html.duty_deferment_account.{duty_deferment_account, duty_deferment_statements_not_available}

import scala.concurrent.Future

class AccountControllerSpec extends SpecBase {

  "showAccountDetails" should {
    "return unauthorized if no session id present" in {
      running(application) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)

        val result = route(application, request).value
        status(result) mustBe SEE_OTHER
      }
    }

    "redirect to the financials homepage if there is no session available" in {
      val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      val application: Application = applicationBuilder()
        .overrides(
          inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(application, request).value
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

      running(application) {
        val request = FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(application, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.AccountController.statementsUnavailablePage("someLink").url
      }
    }

    "display the service unavailable page on a successful response when feature is disabled" in new Setup {
      val config: AppConfig = application.injector.instanceOf[AppConfig]
      config.historicStatementsEnabled = false

      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDocumentService.getDutyDefermentStatements(any, any)(any))
        .thenReturn(Future.successful(dutyDefermentStatementsForEori01))

      when(mockApiConnector.deleteNotification(any, any)(any))
        .thenReturn(Future.successful(true))

      val view: duty_deferment_account = application.injector.instanceOf[duty_deferment_account]

      val request: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, routes.AccountController.showAccountDetails(someLinkId).url)
          .withHeaders("X-Session-Id" -> "someSessionId")

      val messages: Messages = messagesApi.preferred(request)

      val model: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
        "accountNumber",
        Seq(dutyDefermentStatementsForEori01),
        "linkId",
        isNiAccount = false,
        serviceUnavailableUrl
      )(config, messages)

      running(application) {
        val result = route(application, request).value
        status(result) mustBe OK
        contentAsString(result) mustBe view(model)(request, messages, config).toString
      }
    }

    "display historic request url when feature is enabled" in new Setup {
      appConfig.historicStatementsEnabled = true

      val historicRequestUrl: String = appConfig.historicRequestUrl(accountLink.linkId)

      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDocumentService.getDutyDefermentStatements(any, any)(any))
        .thenReturn(Future.successful(dutyDefermentStatementsForEori01))

      when(mockApiConnector.deleteNotification(any, any)(any))
        .thenReturn(Future.successful(true))

      val view: duty_deferment_account =
        application.injector.instanceOf[duty_deferment_account]

      val request: FakeRequest[AnyContentAsEmpty.type] =
        FakeRequest(GET, routes.AccountController.showAccountDetails(someLinkId).url)
          .withHeaders("X-Session-Id" -> "someSessionId")

      val messages: Messages = messagesApi.preferred(request)

      val model: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
        "accountNumber",
        Seq(dutyDefermentStatementsForEori01),
        "linkId",
        isNiAccount = false,
        historicRequestUrl
      )(appConfig, messages)

      running(application) {
        val result = route(application, request).value
        status(result) mustBe OK
        contentAsString(result) mustBe view(model)(request, messages, appConfig).toString
      }
    }

    "statementsUnavailablePage" should {
      "return unauthorized page when no account link found" in {
        val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]

        when(mockSessionCacheConnector.retrieveSession(any, any)(any))
          .thenReturn(Future.successful(None))

        val application: Application = applicationBuilder()
          .overrides(
            inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
          )
          .build()

        running(application) {
          val request = FakeRequest(GET, routes.AccountController.statementsUnavailablePage("someLink").url)
            .withHeaders("X-Session-Id" -> "someSessionId")

          val result = route(application, request).value

          status(result) mustBe UNAUTHORIZED
        }
      }

      "return the accounts unavailable page when an account link found" in new Setup {

        when(mockSessionCacheConnector.retrieveSession(any, any)(any))
          .thenReturn(Future.successful(Some(accountLink)))

        val view        = appWithSessionCache.injector.instanceOf[duty_deferment_statements_not_available]
        val navigatorNA = appWithSessionCache.injector.instanceOf[Navigator]
        val url: String =
          routes.ServiceUnavailableController.onPageLoad(navigatorNA.dutyDefermentStatementNAPageId, someLinkId).url

        running(appWithSessionCache) {
          val request = FakeRequest(GET, routes.AccountController.statementsUnavailablePage(someLinkId).url)
            .withHeaders("X-Session-Id" -> "someSessionId")

          val result = route(appWithSessionCache, request).value

          status(result) mustBe OK
          contentAsString(result) mustBe
            view("accountNumber", someLinkId, Some(url))(request, messages, appConfig).toString()
        }
      }
    }

    trait Setup {
      val mockApiConnector: CustomsFinancialsApiConnector  = mock[CustomsFinancialsApiConnector]
      val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]
      val mockDocumentService: DocumentService             = mock[DocumentService]
      val navigator                                        = new Navigator()
      val serviceUnavailableUrl: String                    =
        routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementPageId, someLinkId).url

      val application: Application = applicationBuilder
        .overrides(
          inject.bind[CustomsFinancialsApiConnector].toInstance(mockApiConnector),
          inject.bind[DocumentService].toInstance(mockDocumentService),
          inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector),
          inject.bind[Navigator].toInstance(navigator)
        )
        .build()

      val appWithSessionCache: Application = applicationBuilder()
        .overrides(
          inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector)
        )
        .build()
    }
  }
}
