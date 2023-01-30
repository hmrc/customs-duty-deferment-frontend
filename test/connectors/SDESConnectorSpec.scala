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
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import util.SpecBase

import scala.concurrent.Future

class SDESConnectorSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "return a sequence of duty deferment files on a successful response" in new Setup {
      val fileInformation: Seq[FileInformation] =
        Seq(FileInformation("someFilename", "downloadUrl", 10L, Metadata(dutyDefermentStatementMetadata1)),
          FileInformation("someFilename2", "downloadUrl", 10L, Metadata(dutyDefermentStatementMetadata2)),
          FileInformation("someFilename3", "downloadUrl", 10L, Metadata(dutyDefermentStatementMetadata3))
        )

      when[Future[Seq[FileInformation]]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(fileInformation))

      running(app) {
        val result = await(connector.getDutyDefermentStatements("someEori", "someDan"))
        result mustBe dutyDefermentStatementFiles
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: SDESConnector = app.injector.instanceOf[SDESConnector]
  }
}
