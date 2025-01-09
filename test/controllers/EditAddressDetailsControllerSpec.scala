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
import play.api.Application
import connectors.CustomsFinancialsApiConnector
import mappings.EditAddressDetailsFormProvider
import models.{EditAddressDetailsUserAnswers, UpdateContactDetailsResponse, UserAnswers}
import pages.EditAddressDetailsPage
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.{AccountLinkCacheService, ContactDetailsCacheService, CountriesProviderService}
import util.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import util.TestImplicits.RemoveCsrf
import views.html.contact_details.edit_address_details
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future

class EditAddressDetailsControllerSpec extends SpecBase {

  "onPageLoad" must {
    "return OK on a valid request" in new Setup {

      val newApp: Application = application(Some(userAnswers))
        .overrides(
          inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
          inject.bind[CountriesProviderService].toInstance(mockCountriesProviderService)
        )
        .build()

      running(newApp) {
        val result = route(newApp, onPageLoadRequest).value

        status(result) mustBe OK

        contentAsString(result).removeCsrf() mustBe view(
          dan = validDan,
          isNi = false,
          form = form.fill(editAddressDetailsUserAnswers),
          countries = fakeCountries
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

      running(application(None)) {
        val result = route(application, invalidSubmitRequest).value
        status(result) mustBe BAD_REQUEST
        contentAsString(result) must include("""<a href="#countryCode"""")
      }
    }

    "return a redirect to session expired when user answers is empty" in new Setup {
      val newApp: Application = application(Some(emptyUserAnswers)).build()
      running(newApp) {
        val result = route(newApp, validSubmitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad.url
      }
    }

    "redirect to the confirmation success page when a successful request made" in new Setup {
      val mockCustomsFinancialsApiConnector: CustomsFinancialsApiConnector = mock[CustomsFinancialsApiConnector]
      val mockContactDetailsCacheServices: ContactDetailsCacheService      = mock[ContactDetailsCacheService]
      val mockAccountLinkCacheService: AccountLinkCacheService             = mock[AccountLinkCacheService]
      val editedUserAnswers: UserAnswers                                   = userAnswers.set(EditAddressDetailsPage, editAddressDetailsUserAnswers).get

      val newApp: Application = application(Some(editedUserAnswers))
        .overrides(
          inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
          inject.bind[CustomsFinancialsApiConnector].toInstance(mockCustomsFinancialsApiConnector),
          inject.bind[ContactDetailsCacheService].toInstance(mockContactDetailsCacheServices),
          inject.bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService)
        )
        .build()

      when(mockUserAnswersCache.store(any, any)(any)).thenReturn(Future.successful(true))
      when(mockContactDetailsCacheServices.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))

      when(mockCustomsFinancialsApiConnector.updateContactDetails(any, any, any, any)(any))
        .thenReturn(Future.successful(UpdateContactDetailsResponse(true)))

      when(mockContactDetailsCacheServices.updateContactDetails(any)(any))
        .thenReturn(Future.successful(true))
      when(mockAccountLinkCacheService.get(any)).thenReturn(Future.successful(Option(dutyDefermentAccountLink)))

      running(newApp) {
        val result = route(newApp, validSubmitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ConfirmContactDetailsController.successAddressDetails().url
      }
    }

    "redirect to the confirmation problem page when a update failed" in new Setup {
      val mockCustomsFinancialsApiConnector: CustomsFinancialsApiConnector = mock[CustomsFinancialsApiConnector]
      val mockContactDetailsCacheServices: ContactDetailsCacheService      = mock[ContactDetailsCacheService]
      val mockAccountLinkCacheService: AccountLinkCacheService             = mock[AccountLinkCacheService]

      val newApp: Application = application(Some(userAnswers))
        .overrides(
          inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
          inject.bind[CustomsFinancialsApiConnector].toInstance(mockCustomsFinancialsApiConnector),
          inject.bind[ContactDetailsCacheService].toInstance(mockContactDetailsCacheServices),
          inject.bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService)
        )
        .build()

      when(mockUserAnswersCache.store(any, any)(any)).thenReturn(Future.successful(true))
      when(mockContactDetailsCacheServices.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))

      when(mockAccountLinkCacheService.get(any)).thenReturn(Future.successful(Option(dutyDefermentAccountLink)))
      when(mockCustomsFinancialsApiConnector.updateContactDetails(any, any, any, any)(any))
        .thenReturn(Future.failed(new RuntimeException("Unknown failure")))

      running(newApp) {
        val result = route(newApp, validSubmitRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.ConfirmContactDetailsController.problem.url
      }
    }
  }

  "isValidCountryName throws invalid country name error" in new Setup {

    val newApp: Application = application(Some(userAnswers))
      .overrides(
        inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache),
        inject.bind[CountriesProviderService].toInstance(mockCountriesProviderService)
      )
      .build()

    running(newApp) {
      val result = route(newApp, invalidCountryCodeRequest).value
      status(result) mustBe 400
    }
  }

  trait Setup {
    val userAnswers: UserAnswers =
      emptyUserAnswers.set(EditAddressDetailsPage, editAddressDetailsUserAnswers).toOption.value

    val onPageLoadRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.EditAddressDetailsController.onPageLoad.url)

    val validSubmitRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditAddressDetailsController.submit.url)
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

    val invalidCountryCodeRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditAddressDetailsController.submit.url)
        .withFormUrlEncodedBody(
          ("dan", validDan),
          ("name", "New Name"),
          ("addressLine1", "123 A New Street"),
          ("postCode", "SW1 6EL"),
          ("countryCode", "P1"),
          ("telephone", "+441111222333"),
          ("countryName", "United Kingdom"),
          ("email", "first.name@email.com")
        )

    val invalidSubmitRequest: FakeRequest[AnyContentAsFormUrlEncoded] =
      fakeRequestWithCsrf(POST, routes.EditAddressDetailsController.submit.url)
        .withFormUrlEncodedBody(
          ("dan", validDan)
        )

    val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]

    def application(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
      new GuiceApplicationBuilder()
        .overrides(
          bind[IdentifierAction].to[FakeIdentifierAction],
          bind[DataRetrievalAction].to(new FakeDataRetrievalAction(userAnswers)),
          bind[Metrics].toInstance(new FakeMetrics)
        )
        .configure(
          "play.filters.csp.nonce.enabled" -> false,
          "auditing.enabled" -> "false",
          "microservice.metrics.graphite.enabled" -> "false",
          "metrics.enabled" -> "false"
        )

    val view: edit_address_details                = application(None).injector.instanceOf[edit_address_details]
    val form: Form[EditAddressDetailsUserAnswers] = application(None).injector.instanceOf[EditAddressDetailsFormProvider].apply()
    val appConfig: AppConfig                      = application(None).injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi                  = application(None).injector.instanceOf[MessagesApi]

  }
}
