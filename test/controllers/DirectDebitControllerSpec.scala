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

import connectors.{DataStoreConnector, SDDSConnector, SessionCacheConnector}
import models.UnverifiedEmail
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.auth.core.retrieve.Email
import util.SpecBase

import scala.concurrent.Future

class DirectDebitControllerSpec extends SpecBase {

  "setup" should {
    "redirect to the financials homepage when no accountLink found" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(None))

      running(app) {
        val request = FakeRequest(GET, routes.DirectDebitController.setup("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "http://localhost:9876/customs/payment-records"
      }
    }

    "return 500 when no email found from the data-store" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Left(UnverifiedEmail)))

      running(app) {
        val request = FakeRequest(GET, routes.DirectDebitController.setup("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return 500 when an error is thrown from SDDS" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Right(Email("some@email.com"))))

      when(mockSDDSConnector.startJourney(any, any)(any))
        .thenReturn(Future.failed(new RuntimeException("Unknown error")))

      running(app) {
        val request = FakeRequest(GET, routes.DirectDebitController.setup("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "redirect to the setup url on a successful response" in new Setup {
      when(mockSessionCacheConnector.retrieveSession(any, any)(any))
        .thenReturn(Future.successful(Some(accountLink)))

      when(mockDataStoreConnector.getEmail(any)(any))
        .thenReturn(Future.successful(Right(Email("some@email.com"))))

      when(mockSDDSConnector.startJourney(any, any)(any))
        .thenReturn(Future.successful("someUrl"))

      running(app) {
        val request = FakeRequest(GET, routes.DirectDebitController.setup("someLink").url)
          .withHeaders("X-Session-Id" -> "someSessionId")

        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe "someUrl"
      }
    }
  }

  trait Setup {
    val mockSDDSConnector: SDDSConnector = mock[SDDSConnector]
    val mockSessionCacheConnector: SessionCacheConnector = mock[SessionCacheConnector]
    val mockDataStoreConnector: DataStoreConnector = mock[DataStoreConnector]


    val app: Application = application().overrides(
      inject.bind[SDDSConnector].toInstance(mockSDDSConnector),
      inject.bind[SessionCacheConnector].toInstance(mockSessionCacheConnector),
      inject.bind[DataStoreConnector].toInstance(mockDataStoreConnector)
    ).build()
  }
}
