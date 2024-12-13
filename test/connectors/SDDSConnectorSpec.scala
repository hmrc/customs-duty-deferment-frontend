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

import models.SDDSResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import util.SpecBase

import scala.concurrent.{ExecutionContext, Future}

class SDDSConnectorSpec extends SpecBase {

  "startJourney" should {
    "return a redirect URL on a successful response" in new Setup {
      val response: SDDSResponse = SDDSResponse("someUrl")

      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[SDDSResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(response))

      when(mockHttpClient.post(any)(any)).thenReturn(requestBuilder)

      running(app) {
        val result = await(connector.startJourney("dan", "some@email.com"))
        result mustBe "someUrl"
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClientV2   = mock[HttpClientV2]
    val requestBuilder: RequestBuilder = mock[RequestBuilder]

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = application()
      .overrides(
        inject.bind[HttpClientV2].toInstance(mockHttpClient)
      )
      .build()

    val connector: SDDSConnector = app.injector.instanceOf[SDDSConnector]
  }
}
