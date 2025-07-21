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
import models.{AccountStatusOpen, DutyDefermentAccountLink, UserAnswers}
import org.mockito.Mockito.when
import pages.{EditAddressDetailsPage, EditContactDetailsPage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.api.{Application, inject}
import services.AccountLinkCacheService
import util.SpecBase
import views.html.contact_details.edit_success_address
import views.html.contact_details.edit_success_contact

import org.mockito.ArgumentMatchers.any
import scala.concurrent.Future
import scala.reflect.ClassTag

class ConfirmContactDetailsControllerSpec extends SpecBase {

  "successAddressDetails" should {

    "return INTERNAL_SERVER_ERROR when user answers is empty for address details" in new Setup {
      running(emptyUsersApp) {
        val result = route(emptyUsersApp, successAddressDetailsRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return OK when user answers has some data" in new Setup {
      val ddAccLink: DutyDefermentAccountLink = DutyDefermentAccountLink(
        eori = validEori,
        dan = validDan,
        linkId = testLinkUrl,
        status = AccountStatusOpen,
        statusId = validStatus,
        isNiAccount = false
      )

      when(mockAccountLinkCacheService.get(any)).thenReturn(Future.successful(Some(ddAccLink)))

      running(appLinkService(userAnswersAddress)) {
        val result = route(appLinkService(userAnswersAddress), successAddressDetailsRequest).value

        status(result) mustBe OK
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers has some data but exception occurs while" +
      " processing account link data" in new Setup {

        when(mockAccountLinkCacheService.get(any)).thenReturn(Future.failed(new RuntimeException("Error occurred")))

        running(appLinkService(userAnswersAddress)) {
          val result = route(appLinkService(userAnswersAddress), successAddressDetailsRequest).value

          status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
  }

  "successContactDetails" must {

    "return INTERNAL_SERVER_ERROR when user answers is empty for contact details" in new Setup {
      running(emptyUsersApp) {
        val result = route(emptyUsersApp, successContactDetailsRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return OK when user answers has some data" in new Setup {
      val ddAccLink: DutyDefermentAccountLink = DutyDefermentAccountLink(
        eori = validEori,
        dan = validDan,
        linkId = testLinkUrl,
        status = AccountStatusOpen,
        statusId = validStatus,
        isNiAccount = false
      )

      when(mockAccountLinkCacheService.get(any)).thenReturn(Future.successful(Some(ddAccLink)))

      running(appLinkService()) {
        val result = route(appLinkService(), successContactDetailsRequest).value

        status(result) mustBe OK
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers has some data but exception occurs while" +
      " processing account link data" in new Setup {

        when(mockAccountLinkCacheService.get(any)).thenReturn(Future.failed(new RuntimeException("Error occurred")))

        running(appLinkService()) {
          val result = route(appLinkService(), successContactDetailsRequest).value
          status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
  }

  "problem" must {
    "return INTERNAL_SERVER_ERROR" in new Setup {
      running(appLinkService()) {
        val result = route(appLinkService(), problemRequest).value
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

    def appLinkService(userAnswersInput: UserAnswers = userAnswers): Application =
      applicationBuilder(Option(userAnswersInput))
        .overrides(
          inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
          inject.bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService)
        )
        .build()

    val emptyUsersApp: Application = applicationBuilder(Some(emptyUserAnswers))
      .overrides(
        inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
        inject.bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService)
      )
      .build()

    val viewAddress: edit_success_address = instanceOf[edit_success_address]
    val viewContact: edit_success_contact = instanceOf[edit_success_contact]
    val messagesApi: MessagesApi          = instanceOf[MessagesApi]
    val messages: Messages                = messagesApi.preferred(successContactDetailsRequest)
    val messagesAddress: Messages         = messagesApi.preferred(successAddressDetailsRequest)

    def instanceOf[T: ClassTag]: T = appLinkService().injector.instanceOf[T]
  }
}
