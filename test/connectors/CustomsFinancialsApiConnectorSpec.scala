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
import models.UpdateContactDetailsResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status
import play.api.test.Helpers._
import play.api.Application
import play.api.inject
import services.AuditingService
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import util.SpecBase
import utils.Utils.emptyString

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

class CustomsFinancialsApiConnectorSpec extends SpecBase {

  "getContactDetails" should {
    "return contact details from the API" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[ContactDetails]], any[ExecutionContext]))
        .thenReturn(Future.successful(validAccountContactDetails))

      when(mockHttpClient.post(any)(any)).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.getContactDetails("someDan", "someEori"))
        result mustBe validAccountContactDetails
      }
    }
  }

  "updateContactDetails" should {
    "return a contact details response" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[UpdateContactDetailsResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(UpdateContactDetailsResponse(true)))

      when(mockHttpClient.post(any[URL]())(any())).thenReturn(requestBuilder)

      when(mockAuditingService.changeContactDetailsAuditEvent(any, any, any)(any))
        .thenReturn(Future.successful(AuditResult.Success))

      running(application) {
        val result =
          await(connector.updateContactDetails("dan", "eori", validAccountContactDetails, contactDetailsUserAnswers))

        result mustBe UpdateContactDetailsResponse(true)
      }
    }
  }

  "deleteNotification" should {
    "return true when the response from the API returns OK" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(HttpResponse.apply(Status.OK, emptyString)))

      when(mockHttpClient.delete(any[URL]())(any())).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe true
      }
    }

    "return false when the response from the API is not OK" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(HttpResponse.apply(Status.NOT_FOUND, emptyString)))

      when(mockHttpClient.delete(any[URL]())(any())).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe false
      }
    }

    "return false when the API is failing with exception" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.failed(new RuntimeException("Failure")))

      when(mockHttpClient.delete(any[URL]())(any())).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe false
      }
    }
  }

  trait Setup {
    implicit val hc: HeaderCarrier           = HeaderCarrier()
    val mockHttpClient: HttpClientV2         = mock[HttpClientV2]
    val requestBuilder: RequestBuilder       = mock[RequestBuilder]
    val mockAuditingService: AuditingService = mock[AuditingService]

    val application: Application = applicationBuilder
      .overrides(
        inject.bind[HttpClientV2].toInstance(mockHttpClient),
        inject.bind[RequestBuilder].toInstance(requestBuilder),
        inject.bind[AuditingService].toInstance(mockAuditingService)
      )
      .build()

    val connector: CustomsFinancialsApiConnector =
      application.injector.instanceOf[CustomsFinancialsApiConnector]
  }
}
