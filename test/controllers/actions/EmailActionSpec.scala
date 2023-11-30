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

package controllers.actions

import connectors.DataStoreConnector
import models.{AuthenticatedRequest, SignedInUser, UnverifiedEmail}
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.retrieve.Email
import uk.gov.hmrc.http.ServiceUnavailableException
import util.SpecBase

import scala.concurrent.Future

class EmailActionSpec extends SpecBase {

  "EmailAction" should {
    "Let requests with validated email through" in new Setup {
      running (app) {
        when(mockDataStoreConnector.getEmail(any)(any)).thenReturn(Future.successful(Right(Email("last.man@standing.co.uk"))))
        val response = await(emailAction.filter(authenticatedRequest))
        response mustBe None
      }
    }

    "Let request through, when getEmail throws service unavailable exception" in new Setup {
      running(app){
        when(mockDataStoreConnector.getEmail(any)(any)).thenReturn(Future.failed(new ServiceUnavailableException("")))
        val response = await(emailAction.filter(authenticatedRequest))
        response mustBe None
      }
    }

    "Redirect users with unvalidated emails" in new Setup {
      running(app) {
        when(mockDataStoreConnector.getEmail(any)(any)).thenReturn(Future.successful(Left(UnverifiedEmail)))
        val response = await(emailAction.filter(authenticatedRequest))
        response.get.header.status mustBe SEE_OTHER
        response.get.header.headers(LOCATION) must include("/verify-your-email")
      }
    }
  }

  trait Setup {
    val mockDataStoreConnector: DataStoreConnector = mock[DataStoreConnector]

    val app = application().overrides(
      inject.bind[DataStoreConnector].toInstance(mockDataStoreConnector)
    ).build()

    val emailAction = app.injector.instanceOf[EmailAction]

    val authenticatedRequest = AuthenticatedRequest(FakeRequest("GET","/"), SignedInUser("someEori", Seq.empty, "internalId"))
  }
}
