/*
 * Copyright 2022 HM Revenue & Customs
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

import cache.UserAnswersCache
import config.AppConfig
import connectors.CustomsFinancialsApiConnector
import models.{UpdateContactDetailsResponse, UserAnswers}
import pages.EditContactDetailsPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.ContactDetailsCacheService
import util.SpecBase
import util.TestImplicits.RemoveCsrf
import views.html.contact_details.check_answers

import scala.concurrent.Future

class CheckAnswersContactDetailsControllerSpec extends SpecBase {

  "onPageLoad" must {

    "return OK on a valid request" in new Setup {
      running(app) {
        val result = route(app, onPageLoadRequest).value
        status(result) mustBe OK
        contentAsString(result).removeCsrf() mustBe
          view(contactDetailsUserAnswers)(
            onPageLoadRequest,
            messages,
            appConfig
          ).toString().removeCsrf()
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers is empty" in new Setup {
      val newApp: Application = application(Some(emptyUserAnswers)).build()
      running(newApp) {
        val result = route(newApp, onPageLoadRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "submit" must {

    "redirect to the confirmation success page when a successful request made" in new Setup {
      when(mockContactDetailsCacheServices.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))
      when(mockCustomsFinancialsApiConnector.updateContactDetails(any, any, any, any)(any))
        .thenReturn(Future.successful(UpdateContactDetailsResponse(true)))
      when(mockContactDetailsCacheServices.updateContactDetails(any)(any))
        .thenReturn(Future.successful(true))

      running(app) {
        val result = route(app, submitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ConfirmContactDetailsController.success.url
      }
    }

    "redirect to the confirmation problem page when an error occurs" in new Setup {
      when(mockContactDetailsCacheServices.getContactDetails(any, any, any)(any))
        .thenReturn(Future.failed(new RuntimeException("Unknown exception")))

      running(app) {
        val result = route(app, submitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ConfirmContactDetailsController.problem.url
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers is empty" in new Setup {
      val newApp: Application = application(Some(emptyUserAnswers)).build()
      running(newApp) {
        val result = route(newApp, submitRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }


  trait Setup {
    val userAnswers: UserAnswers =
      emptyUserAnswers.set(EditContactDetailsPage, contactDetailsUserAnswers).toOption.value

    val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]

    val onPageLoadRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.CheckAnswersContactDetailsController.onPageLoad.url)

    val submitRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(POST, routes.CheckAnswersContactDetailsController.submit.url)

    val mockCustomsFinancialsApiConnector: CustomsFinancialsApiConnector = mock[CustomsFinancialsApiConnector]
    val mockContactDetailsCacheServices: ContactDetailsCacheService = mock[ContactDetailsCacheService]

    lazy val app: Application = application(Some(userAnswers)).overrides(
      inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
      inject.bind[CustomsFinancialsApiConnector].toInstance(mockCustomsFinancialsApiConnector),
      inject.bind[ContactDetailsCacheService].toInstance(mockContactDetailsCacheServices)
    ).build()

    val view: check_answers = app.injector.instanceOf[check_answers]
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    val messages: Messages = messagesApi.preferred(onPageLoadRequest)


  }

}
