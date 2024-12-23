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

import cache.UserAnswersCache
import config.AppConfig
import models.UserAnswers
import pages.{EditAddressDetailsPage, EditContactDetailsPage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.AccountLinkCacheService
import util.SpecBase
import views.html.contact_details.edit_success_address
import views.html.contact_details.edit_success_contact

class ConfirmContactDetailsControllerSpec extends SpecBase {

  "success" must {

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
      fakeRequestWithCsrf(GET, routes.ConfirmContactDetailsController.successContactDetails().url)

    val successAddressDetailsRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ConfirmContactDetailsController.successAddressDetails().url)

    val problemRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ConfirmContactDetailsController.problem.url)

    val mockUserAnswersCache: UserAnswersCache               = mock[UserAnswersCache]
    val mockAccountLinkCacheService: AccountLinkCacheService = mock[AccountLinkCacheService]

    val app: Application = application(Some(userAnswers))
      .overrides(
        inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
        inject.bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService)
      )
      .build()

    val appAddressEdit: Application = application(Some(userAnswersAddress))
      .overrides(
        inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache)
      )
      .build()

    val viewAddress: edit_success_address = app.injector.instanceOf[edit_success_address]
    val viewContact: edit_success_contact = app.injector.instanceOf[edit_success_contact]
    val appConfig: AppConfig              = app.injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi          = app.injector.instanceOf[MessagesApi]
    val messages: Messages                = messagesApi.preferred(successContactDetailsRequest)
    val messagesAddress: Messages         = messagesApi.preferred(successAddressDetailsRequest)
  }
}
