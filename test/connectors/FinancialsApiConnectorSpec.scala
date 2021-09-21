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

import models.FileRole.DutyDefermentStatement
import play.api.http.Status
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import util.SpecBase

import scala.concurrent.Future

class FinancialsApiConnectorSpec extends SpecBase {

  "deleteNotification" should {
    "return true when the response from the API returns OK" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse.apply(Status.OK, "")))

      running(app){
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe true
      }
    }

    "return false when the response from the API is not OK" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse.apply(Status.NOT_FOUND, "")))

      running(app){
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe false
      }
    }
  }

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockHttpClient: HttpClient = mock[HttpClient]

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: FinancialsApiConnector =
      app.injector.instanceOf[FinancialsApiConnector]
  }
}
