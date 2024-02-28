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

package services

import cache.ContactDetailsCache
import connectors.CustomsFinancialsApiConnector
import models.DataRequest
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.{Application, inject}
import util.SpecBase
import play.api.test.Helpers._
import uk.gov.hmrc.http.SessionId

import scala.concurrent.Future

class ContactDetailsCacheServiceSpec extends SpecBase {

  "getContactDetails" must {
    "return cached data when present" in new Setup {
      when(mockContactDetailsCache.retrieve(any)(any))
        .thenReturn(Future.successful(Some(validAccountContactDetails)))

      running(app) {
        val result = await(service.getContactDetails("someInternalId", "someDan", "someEori"))
        result mustBe validAccountContactDetails
      }
    }

    "call the financials api when no cached data present" in new Setup {
      when(mockContactDetailsCache.retrieve(any)(any))
        .thenReturn(Future.successful(None))
      when(mockCustomsFinancialsApiConnector.getContactDetails(any, any)(any))
        .thenReturn(Future.successful(validAccountContactDetails))
      when(mockContactDetailsCache.store(any, any)(any))
        .thenReturn(Future.successful(true))

      running(app) {
        val result = await(service.getContactDetails("someInternalId", "someDan", "someEori"))
        result mustBe validAccountContactDetails
      }
    }
  }

  "updateContactDetails" must {
    "update the cache with the new data" in new Setup {
      when(mockContactDetailsCache.store(any, any)(any))
        .thenReturn(Future.successful(true))

      implicit val dataRequest: DataRequest[AnyContent] = DataRequest(FakeRequest(),
        "identifier",
        "eori",
        SessionId("session"),
        emptyUserAnswers)

      running(app) {
        val result = await(service.updateContactDetails(contactDetailsUserAnswers))
        result mustBe true
      }
    }
  }


  trait Setup {
    val mockContactDetailsCache: ContactDetailsCache = mock[ContactDetailsCache]
    val mockCustomsFinancialsApiConnector: CustomsFinancialsApiConnector = mock[CustomsFinancialsApiConnector]

    val app: Application = application().overrides(
      inject.bind[CustomsFinancialsApiConnector].toInstance(mockCustomsFinancialsApiConnector),
      inject.bind[ContactDetailsCache].toInstance(mockContactDetailsCache)
    ).build()

    val service: ContactDetailsCacheService = app.injector.instanceOf[ContactDetailsCacheService]
  }

}
