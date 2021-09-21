/*
 * Copyright 2021 HM Revenue & Customs
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

import models.{EmailResponse, EoriHistory, EoriHistoryResponse, UndeliverableInformation}
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

      running(app){
        val result = await(connector.getAllEoriHistory("someEori"))
        result mustBe eoriHistoryResponse.eoriHistory
      }
    }

    "return an empty sequence of EORI when the request fails" in new Setup {
      when[Future[EoriHistoryResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.failed(new HttpException("Unknown Error", 500)))

      running(app){
        val result = await(connector.getAllEoriHistory("defaultEori"))
        result mustBe List(EoriHistory("defaultEori", None, None))
      }
    }
  }

  "getEmail" should {
    "return an email address when the request is successful and undeliverable is not present in the response" in new Setup {
      val emailResponse: EmailResponse = EmailResponse(Some("some@email.com"), None, None )

      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(emailResponse))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe Some(Email("some@email.com"))
      }
    }

    "return no email address when the request is successful and undeliverable is present in the response" in new Setup {
      val emailResponse: EmailResponse = EmailResponse(Some("some@email.com"), None, Some(UndeliverableInformation("someSubject")))

      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(emailResponse))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe None
      }
    }

    "return no email when a NOT_FOUND response is returned" in new Setup {
      when[Future[EmailResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.failed(UpstreamErrorResponse("Not Found", 404, 404)))

      running(app) {
        val result = await(connector.getEmail("someEori"))
        result mustBe None
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val eoriHistoryResponse: EoriHistoryResponse =
      EoriHistoryResponse(Seq(EoriHistory("someEori", None, None)))

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: DataStoreConnector = app.injector.instanceOf[DataStoreConnector]
  }
}
