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

package connectors

import models.FileRole.DutyDefermentStatement
import models.responses.retrieve.ContactDetails
import models.{EmailUnverifiedResponse, EmailVerifiedResponse, GetContactDetailsRequest, UpdateContactDetailsRequest, UpdateContactDetailsResponse}
import play.api.http.Status
import play.api.test.Helpers._
import play.api.{Application, inject}
import services.AuditingService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import util.SpecBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsFinancialsApiConnectorSpec extends SpecBase {

  "getContactDetails" should {
    "return contact details from the API" in new Setup {
      when(mockHttpClient.POST[GetContactDetailsRequest, ContactDetails](any, any, any)(any, any, any, any))
        .thenReturn(Future.successful(validAccountContactDetails))

      running(app) {
        val result = await(connector.getContactDetails("someDan", "someEori"))
        result mustBe validAccountContactDetails
      }
    }
  }

  "updateContactDetails" should {
    "return a contact details response" in new Setup {
      when(mockHttpClient.POST[UpdateContactDetailsRequest, UpdateContactDetailsResponse](any, any, any)(any, any, any, any))
        .thenReturn(Future.successful(UpdateContactDetailsResponse(true)))

      when(mockAuditingService.changeContactDetailsAuditEvent(any, any, any)(any))
        .thenReturn(Future.successful(AuditResult.Success))

      running(app) {
        val result = await(connector.updateContactDetails("dan", "eori", validAccountContactDetails, contactDetailsUserAnswers))
        result mustBe UpdateContactDetailsResponse(true)
      }
    }
  }

  "deleteNotification" should {
    "return true when the response from the API returns OK" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse.apply(Status.OK, "")))

      running(app) {
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe true
      }
    }

    "return false when the response from the API is not OK" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse.apply(Status.NOT_FOUND, "")))

      running(app) {
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe false
      }
    }

    "return false when the API is failing with exception" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.failed(new RuntimeException("Failure")))

      running(app) {
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe false
      }
    }
  }

  "isEmailVerified" should {
    "return unverified email" in new Setup {
      when[Future[EmailUnverifiedResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(EmailUnverifiedResponse(Some("unverified@email.com"))))

      running(app) {
        val result = await(connector.isEmailUnverified(hc))
        result mustBe Some("unverified@email.com")
      }
    }
  }

  "VerifiedEmail" should {
    "return undelivered email" in new Setup {
      when[Future[EmailVerifiedResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(EmailVerifiedResponse(Some("test@test.com"))))

      running(app) {
        connector.verifiedEmail.map(_.verifiedEmail mustBe Some("test@test.com"))
      }
    }
  }

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockHttpClient: HttpClient = mock[HttpClient]
    val mockAuditingService: AuditingService = mock[AuditingService]

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient),
      inject.bind[AuditingService].toInstance(mockAuditingService)
    ).build()

    val connector: CustomsFinancialsApiConnector =
      app.injector.instanceOf[CustomsFinancialsApiConnector]
  }
}
