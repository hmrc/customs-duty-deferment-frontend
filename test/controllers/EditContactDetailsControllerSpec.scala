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

import cache.UserAnswersCache
import config.AppConfig
import mappings.EditContactDetailsFormProvider
import models.{ContactDetailsUserAnswers, UserAnswers}
import pages.EditContactDetailsPage
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.CountriesProviderService
import util.SpecBase
import util.TestImplicits.RemoveCsrf
import views.html.contact_details.edit

import scala.concurrent.Future

class EditContactDetailsControllerSpec extends SpecBase {

  "onPageLoad" must {
    "return OK on a valid request" in new Setup {

      val newApp: Application = application(Some(userAnswers)).overrides(
        inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
        inject.bind[CountriesProviderService].toInstance(mockCountriesProviderService)
      ).build()


      running(newApp) {
        val result = route(newApp, onPageLoadRequest).value
        status(result) mustBe OK

        contentAsString(result).removeCsrf() mustBe view(
          validDan,
          form.fill(contactDetailsUserAnswers),
          fakeCountries
        )(onPageLoadRequest, messages, appConfig).toString().removeCsrf()
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
    "return BAD_REQUEST when form errors occur" in new Setup {
      running(app) {
        val result = route(app, invalidSubmitRequest).value
        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("""<a href="#countryCode"""")
      }
    }

    "return a redirect to session expired when user answers is empty" in new Setup {
      val newApp: Application = application(Some(emptyUserAnswers)).build()
      running(newApp) {
        val result = route(newApp, validSubmitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url
      }
    }

    "return a redirect to confirm with valid information" in new Setup {
      when(mockUserAnswersCache.store(any, any)(any)).thenReturn(Future.successful(true))
      running(app) {
        val result = route(app, validSubmitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.CheckAnswersContactDetailsController.onPageLoad().url
      }
    }
  }

  trait Setup {
    val userAnswers: UserAnswers =
      emptyUserAnswers.set(EditContactDetailsPage, contactDetailsUserAnswers).toOption.value

    val onPageLoadRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.EditContactDetailsController.onPageLoad().url)

    val validSubmitRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditContactDetailsController.submit().url)
        .withFormUrlEncodedBody(
          ("dan", validDan),
          ("name", "New Name"),
          ("addressLine1", "123 A New Street"),
          ("postCode", "SW1 6EL"),
          ("countryCode", "GB"),
          ("telephone", "+441111222333"),
          ("countryName", "United Kingdom"),
          ("email", "first.name@email.com")
        )

    val invalidSubmitRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditContactDetailsController.submit().url)
        .withFormUrlEncodedBody(
          ("dan", validDan)
        )

    val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]

    lazy val app: Application = application(Some(userAnswers)).overrides(
      inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache)
    ).build()

    val view: edit = app.injector.instanceOf[edit]
    val form: Form[ContactDetailsUserAnswers] = app.injector.instanceOf[EditContactDetailsFormProvider].apply()
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    val messages: Messages = messagesApi.preferred(onPageLoadRequest)
  }
}