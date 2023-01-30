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

import cache.AccountLinkCache
import connectors.SessionCacheConnector
import models.{AccountLink, AccountStatusOpen, DefermentAccountAvailable, DutyDefermentAccountLink}
import play.api.test.Helpers._
import play.api.{Application, inject}
import util.SpecBase

import scala.concurrent.Future

class AccountLinkCacheServiceSpec extends SpecBase {

  "cacheAccountLink" must {
    "return NoDutyDefermentSessionAvailable when no session returned" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      running(app) {
        val result = await(service.cacheAccountLink("someLinkId", "someSessionId", "someInternalId"))
        result mustBe Left(NoDutyDefermentSessionAvailable)
      }
    }

    "return NoDutyDefermentSessionAvailable when no accountStatusId present" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(AccountLink("dan", "linkId", AccountStatusOpen, None))))

      running(app) {
        val result = await(service.cacheAccountLink("someLinkId", "someSessionId", "someInternalId"))
        result mustBe Left(NoDutyDefermentSessionAvailable)
      }
    }

    "return AccountLink on successful submission" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(AccountLink("dan", "linkId", AccountStatusOpen, Some(DefermentAccountAvailable)))))
      when(mockAccountLinkCache.store(any, any)(any))
        .thenReturn(Future.successful(true))

      running(app) {
        val result = await(service.cacheAccountLink("someLinkId", "someSessionId", "someInternalId"))
        result mustBe Right(dutyDefermentAccountLink)
      }
    }
  }

  "get" must {
    "return the result from accountLinkCache" in new Setup {

      when(mockAccountLinkCache.retrieve(any)(any))
        .thenReturn(Future.successful(Some(dutyDefermentAccountLink)))

      running(app) {
        val result = await(service.get("someInternalId"))
        result mustBe Some(dutyDefermentAccountLink)
      }
    }
  }

  "remove" must {
    "return a boolean based on write result" in new Setup {
      when(mockAccountLinkCache.remove(any[String]))
        .thenReturn(Future.successful(true))

      running(app) {
        val result = await(service.remove("someInternalId"))
        result mustBe true
      }
    }
  }


  trait Setup {
    val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]
    val mockAccountLinkCache: AccountLinkCache = mock[AccountLinkCache]
    val dutyDefermentAccountLink: DutyDefermentAccountLink = DutyDefermentAccountLink("dan", "someLinkId", AccountStatusOpen, DefermentAccountAvailable)

    val app: Application = application().overrides(
      inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector),
      inject.bind[AccountLinkCache].toInstance(mockAccountLinkCache)
    ).build()

    val service: AccountLinkCacheService = app.injector.instanceOf[AccountLinkCacheService]
  }
}
