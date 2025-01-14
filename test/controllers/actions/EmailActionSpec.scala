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
import models.{AuthenticatedRequest, SignedInUser, UndeliverableEmail, UnverifiedEmail}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.auth.core.retrieve.Email
import uk.gov.hmrc.http.ServiceUnavailableException
import util.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmailActionSpec extends SpecBase {

  "EmailAction" should {
    "Let requests with validated email through" in new Setup {
      running(application) {
        when(mockDataStoreConnector.getEmail(any)(any))
          .thenReturn(Future.successful(Right(Email("last.man@standing.co.uk"))))

        val response = await(emailAction.filter(authenticatedRequest))
        response mustBe None
      }
    }

    "Let request through, when getEmail throws service unavailable exception" in new Setup {
      running(application) {
        when(mockDataStoreConnector.getEmail(any)(any)).thenReturn(Future.failed(new ServiceUnavailableException("")))

        val response = await(emailAction.filter(authenticatedRequest))
        response mustBe None
      }
    }

    "Redirect users with unvalidated emails" in new Setup {
      running(application) {
        when(mockDataStoreConnector.getEmail(any)(any)).thenReturn(Future.successful(Left(UnverifiedEmail)))

        val response = await(emailAction.filter(authenticatedRequest))
        response.get.header.status mustBe SEE_OTHER
        response.get.header.headers(LOCATION) must include("/verify-your-email")
      }
    }

    "Redirect users to undelivered email page when undeliverable email response is returned" in new Setup {
      running(application) {
        when(mockDataStoreConnector.getEmail(any)(any))
          .thenReturn(Future.successful(Left(UndeliverableEmail("test@test.com"))))

        val response = emailAction.filter(authenticatedRequest).map(a => a.get)
        status(response) mustBe SEE_OTHER
        redirectLocation(response) mustBe Some(controllers.routes.EmailController.showUndeliverable().url)
      }
    }
  }

  trait Setup {
    val mockDataStoreConnector: DataStoreConnector = mock[DataStoreConnector]

    val application: Application = applicationBuilder()
      .overrides(
        inject.bind[DataStoreConnector].toInstance(mockDataStoreConnector)
      )
      .build()

    val emailAction: EmailAction = application.injector.instanceOf[EmailAction]

    val authenticatedRequest: AuthenticatedRequest[AnyContentAsEmpty.type] =
      AuthenticatedRequest(FakeRequest("GET", "/"), SignedInUser("someEori", Seq.empty, "internalId"))
  }
}
