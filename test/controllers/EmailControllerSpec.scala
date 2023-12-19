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

package controllers

import connectors.CustomsFinancialsApiConnector
import models.{EmailUnverifiedResponse, EmailVerifiedResponse}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.OK
import play.api.{Application, inject}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import util.SpecBase

import scala.concurrent.Future

class EmailControllerSpec extends SpecBase {

  "showUnverified" must {
    "return unverified email" in new Setup {

      when[Future[EmailUnverifiedResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(response))

      running(app) {
        val connector = app.injector.instanceOf[CustomsFinancialsApiConnector]

        val result: Future[Option[String]] = connector.isEmailUnverified(hc)
        await(result) mustBe expectedResult
      }
    }

    "return unverified email response" in new Setup {

      when[Future[EmailUnverifiedResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(response))

      running(app) {
        val request = fakeRequest(GET, routes.EmailController.showUnverified().url)
        val result = route(app, request).value
        status(result) shouldBe OK
      }
    }
  }

  "showUndeliverable" must {
    "display undelivered email page" in new Setup {

      val verifiedResponse: EmailVerifiedResponse = EmailVerifiedResponse(Some("test@test.com"))

      when[Future[EmailVerifiedResponse]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(verifiedResponse))

      running(app) {
        val request = fakeRequest(GET, routes.EmailController.showUndeliverable().url)
        val result = route(app, request).value
        status(result) shouldBe OK
      }
    }
  }

  trait Setup {
    val expectedResult: Option[String] = Some("unverifiedEmail")
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockHttpClient: HttpClient = mock[HttpClient]

    val response: EmailUnverifiedResponse = EmailUnverifiedResponse(Some("unverifiedEmail"))

    val app: Application = application().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()
  }
}
