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
import config.{AppConfig, ErrorHandler}
import models.UserAnswers
import pages.{EditAddressDetailsPage, EditContactDetailsPage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import util.SpecBase
import util.TestImplicits.RemoveCsrf
import views.html.contact_details.edit_success

import scala.concurrent.Future

class ConfirmContactDetailsControllerSpec extends SpecBase {

  "success" must {
    "return OK on a successful edit contact details submission" in new Setup {
      when(mockUserAnswersCache.remove(any[String]))
        .thenReturn(Future.successful(true))

      running(app) {
        val result = route(app, successContactDetailsRequest).value
        status(result) mustBe OK
        contentAsString(result).removeCsrf() mustBe view(validDan)(
          successContactDetailsRequest,
          messages,
          appConfig
        ).toString().removeCsrf()
      }
    }

    "return OK on a successful edit address details submission" in new Setup {
      when(mockUserAnswersCache.remove(any[String]))
        .thenReturn(Future.successful(true))

      running(appAddressEdit) {
        val result = route(appAddressEdit, successAddressDetailsRequest).value
        status(result) mustBe OK
        contentAsString(result).removeCsrf() mustBe view(validDan)(
          successAddressDetailsRequest,
          messagesAddress,
          appConfig
        ).toString().removeCsrf()
      }
    }

    "return OK on a successful submission when the cache returns false for contact details" in new Setup {
      when(mockUserAnswersCache.remove(any[String]))
        .thenReturn(Future.successful(false))

      running(app) {
        val result = route(app, successContactDetailsRequest).value
        status(result) mustBe OK
        contentAsString(result).removeCsrf() mustBe view(validDan)(
          successContactDetailsRequest,
          messages,
          appConfig
        ).toString().removeCsrf()
      }
    }

    "return OK on a successful submission when the cache returns false for address details" in new Setup {
      when(mockUserAnswersCache.remove(any[String]))
        .thenReturn(Future.successful(false))

      running(app) {
        val result = route(appAddressEdit, successAddressDetailsRequest).value
        status(result) mustBe OK
        contentAsString(result).removeCsrf() mustBe view(validDan)(
          successAddressDetailsRequest,
          messages,
          appConfig
        ).toString().removeCsrf()
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers is empty for contact details" in new Setup {
      val newApp: Application = application(Some(emptyUserAnswers)).build()
      running(newApp) {
        val result = route(newApp, successContactDetailsRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers is empty for address details" in new Setup {
      val newApp: Application = application(Some(emptyUserAnswers)).build()
      running(newApp) {
        val result = route(newApp, successAddressDetailsRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "problem" must {
    "return INTERNAL_SERVER_ERROR" in new Setup {
      running(app) {
        val result = route(app, problemRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  trait Setup {
    val userAnswers: UserAnswers =
      emptyUserAnswers.set(EditContactDetailsPage, editContactDetailsUserAnswers).toOption.value

    val userAnswersAddress: UserAnswers =
      emptyUserAnswers.set(EditAddressDetailsPage, editAddressDetailsUserAnswers).toOption.value

    val successContactDetailsRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ConfirmContactDetailsController.successContactDetails.url)

    val successAddressDetailsRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ConfirmContactDetailsController.successAddressDetails.url)

    val problemRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ConfirmContactDetailsController.problem.url)

    val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]

    val app: Application = application(Some(userAnswers)).overrides(
      inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache)
    ).build()

    val appAddressEdit: Application = application(Some(userAnswersAddress)).overrides(
      inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache)
    ).build()

    val view: edit_success = app.injector.instanceOf[edit_success]
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    val messages: Messages = messagesApi.preferred(successContactDetailsRequest)
    val messagesAddress: Messages = messagesApi.preferred(successAddressDetailsRequest)
  }
}
