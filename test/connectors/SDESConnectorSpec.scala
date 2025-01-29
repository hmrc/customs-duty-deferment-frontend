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

import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import util.SpecBase

import scala.concurrent.Future

class SDESConnectorSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "return a sequence of duty deferment files on a successful response" in new Setup {
      val fileSize                              = 10L
      val fileInformation: Seq[FileInformation] =
        Seq(
          FileInformation("someFilename", "downloadUrl", fileSize, Metadata(dutyDefermentStatementMetadata1)),
          FileInformation("someFilename2", "downloadUrl", fileSize, Metadata(dutyDefermentStatementMetadata2)),
          FileInformation("someFilename3", "downloadUrl", fileSize, Metadata(dutyDefermentStatementMetadata3)),
          FileInformation("someFilename4", "downloadUrl", fileSize, Metadata(dutyDefermentStatementMetadata4))
        )

      when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
      when(requestBuilder.setHeader(any[(String, String)]())).thenReturn(requestBuilder)
      when(mockHttpClient.get(any)(any)).thenReturn(requestBuilder)

      when(requestBuilder.execute(any, any))
        .thenReturn(Future.successful(fileInformation))

      running(application) {
        val result = await(connector.getDutyDefermentStatements("someEori", "someDan"))
        result mustBe dutyDefermentStatementFiles
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClientV2   = mock[HttpClientV2]
    val requestBuilder: RequestBuilder = mock[RequestBuilder]

    val application: Application = applicationBuilder
      .overrides(
        inject.bind[HttpClientV2].toInstance(mockHttpClient)
      )
      .build()

    val connector: SDESConnector = application.injector.instanceOf[SDESConnector]
  }
}
