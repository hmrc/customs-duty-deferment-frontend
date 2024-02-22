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

import models.{EmailResponse, EoriHistory, EoriHistoryResponse, UndeliverableEmail, UndeliverableInformation, UndeliverableInformationEvent, UnverifiedEmail}
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.auth.core.retrieve.Email
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpException, UpstreamErrorResponse}
import util.SpecBase

import scala.concurrent.Future

class DataStoreConnectorSpec extends SpecBase {

  "getAllEoriHistory" should {
    "return a sequence of eori history when the request is successful" in new Setup {
      when[Future[EoriHistoryResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(eoriHistoryResponse))

      running(app) {
        val result = await(connector.getAllEoriHistory("someEori"))
        result mustBe eoriHistoryResponse.eoriHistory
      }
    }

    "return an empty sequence of EORI when the request fails" in new Setup {
      when[Future[EoriHistoryResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.failed(new HttpException("Unknown Error", INTERNAL_SERVER_ERROR)))

      running(app) {
        val result = await(connector.getAllEoriHistory("defaultEori"))
        result mustBe List(EoriHistory("defaultEori", None, None))
      }
    }
  }

  "getEmail" should {
    "return an email address when the request is successful and undeliverable is not present in the response" in new Setup {
      val emailResponse: EmailResponse = EmailResponse(Some("some@email.com"), None, None)

      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(emailResponse))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe Right(Email("some@email.com"))
      }
    }

    "return no email address when the request is successful and undeliverable is present in the response" in new Setup {
      val emailResponse: EmailResponse = EmailResponse(Some("some@email.com"), None, Some(undelInfoOb))

      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(emailResponse))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe Left(UndeliverableEmail("some@email.com"))
      }
    }

    "return unverifiedEmail when the request is successful and email address is not present in the response" in new Setup {
      val emailResponse: EmailResponse = EmailResponse(None, None, None)

      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(emailResponse))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe Left(UnverifiedEmail)
      }
    }

    "return no email when a NOT_FOUND response is returned" in new Setup {
      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.failed(UpstreamErrorResponse("Not Found", NOT_FOUND, NOT_FOUND)))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe Left(UnverifiedEmail)
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val eoriHistoryResponse: EoriHistoryResponse =
      EoriHistoryResponse(Seq(EoriHistory("someEori", None, None)))

    val undelInfoEventOb: UndeliverableInformationEvent = UndeliverableInformationEvent("example-id",
      "someEvent",
      "email@email.com",
      "2021-05-14T10:59:45.811+01:00",
      Some(12),
      Some("Inbox full"),
      "HMRC-CUS-ORG~EORINumber~GB744638982004")

    val undelInfoOb: UndeliverableInformation = UndeliverableInformation("someSubject",
      "example-id",
      "example-group-id",
      "2021-05-14T10:59:45.811+01:00",
      undelInfoEventOb)

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: DataStoreConnector = app.injector.instanceOf[DataStoreConnector]
  }
}
