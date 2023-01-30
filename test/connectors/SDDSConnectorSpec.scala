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
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import util.SpecBase

import scala.concurrent.Future

class SDDSConnectorSpec extends SpecBase {

  "startJourney" should {
    "return a redirect URL on a successful response" in new Setup {
      val response: SDDSResponse = SDDSResponse("someUrl")

      when[Future[SDDSResponse]](mockHttpClient.POST(any, any, any)(any, any, any, any))
        .thenReturn(Future.successful(response))

      running(app){
        val result = await(connector.startJourney("dan", "some@email.com"))
        result mustBe "someUrl"
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: SDDSConnector = app.injector.instanceOf[SDDSConnector]
  }
}
