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
import mappings.EditContactDetailsFormProvider
import models.{EditContactDetailsUserAnswers, UserAnswers}
import pages.EditContactDetailsPage
import play.api.Application
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import util.SpecBase
import util.TestImplicits.RemoveCsrf
import views.html.contact_details.edit_contact_details

class EditContactDetailsControllerSpec extends SpecBase {

  "onPageLoad" must {

    "return OK on a val id request" in new Setup {
      running(appWithUserAnswers) {
        val result = route(appWithUserAnswers, onPageLoadRequest).value
        status(result) mustBe OK

        contentAsString(result).removeCsrf() mustBe view(
          validDan,
          isNi = false,
          form.fill(editContactDetailsUserAnswers),
          fakeCountries
        )(onPageLoadRequest, messages, appConfig).toString().removeCsrf()
      }
    }

    "return INTERNAL_SERVER_ERROR when user answers is empty" in new Setup {
      running(appWithEmptyUserAnswers) {
        val result = route(appWithEmptyUserAnswers, onPageLoadRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "submit" must {

    "return BAD_REQUEST when form errors occur" in new Setup {
      running(appWithoutUsersAnswers) {
        val result = route(appWithoutUsersAnswers, invalidSubmitRequest).value
        status(result) mustBe BAD_REQUEST
      }
    }

    "return a redirect to session expired when user answers is empty" in new Setup {
      running(appWithEmptyUserAnswers) {
        val result = route(appWithEmptyUserAnswers, validSubmitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad.url
      }
    }
  }

  trait Setup {
    val userAnswers: UserAnswers =
      emptyUserAnswers.set(EditContactDetailsPage, editContactDetailsUserAnswers).toOption.value

    val onPageLoadRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.EditContactDetailsController.onPageLoad.url)

    val validSubmitRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditContactDetailsController.submit.url)
        .withFormUrlEncodedBody(
          ("dan", validDan),
          ("name", "New Name"),
          ("telephone", "+441111222333"),
          ("email", "first.name@email.com"),
          ("isNiAccount", "false")
        )

    val invalidSubmitRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditContactDetailsController.submit.url)
        .withFormUrlEncodedBody(
          ("dan", validDan)
        )

    val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]
    val appWithUserAnswers: Application        = application(Some(userAnswers))
    val appWithEmptyUserAnswers: Application   = application(Some(emptyUserAnswers))
    val appWithoutUsersAnswers: Application    = application()

    val messagesApi: MessagesApi = application(Some(userAnswers)).injector.instanceOf[MessagesApi]
    val messages: Messages       = messagesApi.preferred(onPageLoadRequest)

    val form: Form[EditContactDetailsUserAnswers] =
      application(Some(userAnswers)).injector.instanceOf[EditContactDetailsFormProvider].apply()

    val view: edit_contact_details =
      application(Some(userAnswers)).injector.instanceOf[edit_contact_details]
  }
}
