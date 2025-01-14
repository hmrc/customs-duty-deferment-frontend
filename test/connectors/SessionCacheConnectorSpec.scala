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

import models.{AccountLink, AccountStatusOpen}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, UpstreamErrorResponse}
import util.SpecBase
import utils.Utils.emptyString

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

class SessionCacheConnectorSpec extends SpecBase {

  "retrieveSession" should {
    "return an account link on a successful response" in new Setup {
      val link: AccountLink = AccountLink("someEori", "12345", "someId", AccountStatusOpen, None, isNiAccount = false)

      when(requestBuilder.execute(any[HttpReads[AccountLink]], any[ExecutionContext]))
        .thenReturn(Future.successful(link))

      when(mockHttpClient.get(any)(any)).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.retrieveSession("someId", "someLink"))
        result mustBe Some(link)
      }
    }

    "return None on a failed response" in new Setup {
      when(requestBuilder.execute(any[HttpReads[AccountLink]], any[ExecutionContext]))
        .thenReturn(Future.failed(UpstreamErrorResponse("Not Found", NOT_FOUND, NOT_FOUND)))

      when(mockHttpClient.get(any)(any)).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.retrieveSession("someId", "someLink"))
        result mustBe None
      }
    }
  }

  "removeSession" should {
    "return true on a successful response from the API" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(HttpResponse(Status.OK, emptyString)))

      when(mockHttpClient.delete(any[URL]())(any())).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.removeSession("someId"))
        result mustBe true
      }
    }

    "return false if the api call fails" in new Setup {
      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext]))
        .thenReturn(Future.failed(new RuntimeException("something went wrong")))

      when(mockHttpClient.delete(any[URL]())(any())).thenReturn(requestBuilder)

      running(application) {
        val result = await(connector.removeSession("someId"))
        result mustBe false
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClientV2   = mock[HttpClientV2]
    val requestBuilder: RequestBuilder = mock[RequestBuilder]
    implicit val hc: HeaderCarrier     = HeaderCarrier()

    val application: Application = applicationBuilder()
      .overrides(
        inject.bind[HttpClientV2].toInstance(mockHttpClient),
        inject.bind[RequestBuilder].toInstance(requestBuilder)
      )
      .build()

    val connector: SessionCacheConnector =
      application.injector.instanceOf[SessionCacheConnector]
  }
}
