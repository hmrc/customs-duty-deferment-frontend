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

import connectors.DataStoreConnector
import models.{UndeliverableEmail, UnverifiedEmail}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AccountLinkCacheService, ContactDetailsCacheService, NoAccountStatusId}
import uk.gov.hmrc.auth.core.retrieve.Email
import util.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import viewmodels.ContactDetailsViewModel
import views.html.contact_details.{show, show_error}

import scala.concurrent.Future

class ShowContactDetailsControllerSpec extends SpecBase {

  "show" must {
    "display error view on exception" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))
      when(mockContactDetailsCacheService.getContactDetails(any, any, any)(any))
        .thenReturn(Future.failed(new RuntimeException("Unknown Error")))

      running(application) {
        val result: Future[Result] = route(application, showRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) mustBe errorView(
          dutyDefermentAccountLink.dan,
          Some(appConfig.financialsHomepage),
          dutyDefermentAccountLink.statusId,
          dutyDefermentAccountLink.linkId
        )(showRequest, messages, appConfig).toString()
      }
    }

    "redirect to session expired view when no accountLink found" in new Setup {
      when(mockAccountLinkCacheService.get(any)).thenReturn(Future.successful(None))

      running(application) {
        val result: Future[Result] = route(application, showRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad.url
      }
    }

    "return OK and the correct view for a GET, with a correctly encrypted DAN" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))

      when(mockContactDetailsCacheService.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))

      running(application) {
        val result: Future[Result] = route(application, showRequest).value
        status(result) mustBe OK
      }
    }
  }

  "startSession" must {
    "redirect to session expired when no account link found" in new Setup {
      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Right(Email("test@test.com"))))
      when(mockAccountLinkCacheService.cacheAccountLink(any, any, any)(any))
        .thenReturn(Future.successful(Left(NoAccountStatusId)))

      running(application) {
        val result = route(application, startSessionRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad.url
      }
    }

    "redirect to 'show' when a verified email response and account link are returned" in new Setup {
      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Right(Email("test@test.com"))))
      when(mockAccountLinkCacheService.cacheAccountLink(any, any, any)(any))
        .thenReturn(Future.successful(Right(dutyDefermentAccountLink)))

      running(application) {
        val result = route(application, startSessionRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ShowContactDetailsController.show().url
      }
    }

    "redirect to 'verify your email' page when an unverified email response is received" in new Setup {
      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Left(UnverifiedEmail)))

      running(application) {
        val result = route(application, startSessionRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.EmailController.showUnverified().url
      }
    }

    "redirect to 'Undelivered email' page when an undelivered email response is received" in new Setup {
      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Left(UndeliverableEmail("test@test.com"))))

      running(application) {
        val result = route(application, startSessionRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.EmailController.showUndeliverable().url
      }
    }

    "redirect to 'show contact details' page when an error occurs while retrieving the email" in new Setup {
      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.failed(new RuntimeException("Error occurred")))
      when(mockAccountLinkCacheService.cacheAccountLink(any, any, any)(any))
        .thenReturn(Future.successful(Right(dutyDefermentAccountLink)))

      running(application) {
        val result = route(application, startSessionRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ShowContactDetailsController.show().url
      }
    }
  }

  trait Setup {
    val mockContactDetailsCacheService: ContactDetailsCacheService = mock[ContactDetailsCacheService]
    val mockAccountLinkCacheService: AccountLinkCacheService       = mock[AccountLinkCacheService]
    val mockDataStoreConnector: DataStoreConnector                 = mock[DataStoreConnector]

    val validContactDetailsViewModel: ContactDetailsViewModel = ContactDetailsViewModel(
      validDan,
      validAccountContactDetails,
      _ => Some("United Kingdom")
    )

    val application: Application = applicationBuilder(None)
      .overrides(
        bind[ContactDetailsCacheService].toInstance(mockContactDetailsCacheService),
        bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService),
        bind[DataStoreConnector].toInstance(mockDataStoreConnector)
      )
      .build()

    val showRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequest(GET, routes.ShowContactDetailsController.show().url)

    val startSessionRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequest(GET, routes.ShowContactDetailsController.startSession("someLinkId").url)

    val view: show            = application.injector.instanceOf[show]
    val errorView: show_error = application.injector.instanceOf[show_error]
    val messagesApi: MessagesApi = application.injector.instanceOf[MessagesApi]
    val messages: Messages       = messagesApi.preferred(showRequest)
  }
}
