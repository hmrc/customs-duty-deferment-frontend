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
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.{AccountLinkCacheService, ContactDetailsCacheService, CountriesProviderService}
import util.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import scala.concurrent.Future

class ContactDetailsStartControllerSpec extends SpecBase {

  "start" must {
    "redirect to the financials homepage if no account link found" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(None))

      running(application) {
        val result = route(application, startEditContactDetailsRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "http://localhost:9876/customs/payment-records"
      }
    }

    "return INTERNAL_SERVER_ERROR when failed to store UserAnswers" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))
      when(mockContactDetailsCacheService.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))
      when(mockCountryProviderService.getCountryName(any))
        .thenReturn(Some("United Kingdom"))
      when(mockUserAnswersCache.store(any, any)(any))
        .thenReturn(Future.failed(new RuntimeException("Unknown Error")))

      running(application) {
        val result = route(application, startEditContactDetailsRequest).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "redirect to edit page on successful submission" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))
      when(mockContactDetailsCacheService.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))
      when(mockCountryProviderService.getCountryName(any))
        .thenReturn(Some("United Kingdom"))
      when(mockUserAnswersCache.store(any, any)(any))
        .thenReturn(Future.successful(true))

      running(application) {
        val result = route(application, startEditContactDetailsRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.EditContactDetailsController.onPageLoad.url
      }
    }

    "redirect to edit address page on successful submission" in new Setup {
      when(mockAccountLinkCacheService.get(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))
      when(mockContactDetailsCacheService.getContactDetails(any, any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))
      when(mockCountryProviderService.getCountryName(any))
        .thenReturn(Some("United Kingdom"))
      when(mockUserAnswersCache.store(any, any)(any))
        .thenReturn(Future.successful(true))

      running(application) {
        val result = route(application, startEditAddressDetailsRequest).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.EditAddressDetailsController.onPageLoad.url
      }
    }
  }

  trait Setup {
    val mockContactDetailsCacheService: ContactDetailsCacheService = mock[ContactDetailsCacheService]
    val mockAccountLinkCacheService: AccountLinkCacheService       = mock[AccountLinkCacheService]
    val mockCountryProviderService: CountriesProviderService       = mock[CountriesProviderService]
    val mockUserAnswersCache: UserAnswersCache                     = mock[UserAnswersCache]

    val startEditContactDetailsRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ContactDetailsEditStartController.start(true).url)

    val startEditAddressDetailsRequest: FakeRequest[AnyContentAsEmpty.type] =
      fakeRequestWithCsrf(GET, routes.ContactDetailsEditStartController.start(false).url)

    val application: Application = applicationBuilder
      .overrides(
        inject.bind[ContactDetailsCacheService].toInstance(mockContactDetailsCacheService),
        inject.bind[AccountLinkCacheService].toInstance(mockAccountLinkCacheService),
        inject.bind[CountriesProviderService].toInstance(mockCountryProviderService),
        inject.bind[UserAnswersCache].toInstance(mockUserAnswersCache)
      )
      .build()
  }
}
