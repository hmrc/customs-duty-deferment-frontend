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

import connectors.DataStoreConnector
import models.{EmailUnverifiedResponse, EmailVerifiedResponse}
import play.api.http.Status.OK
import play.api.{Application, inject}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpReads
import util.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.shouldBe
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{ExecutionContext, Future}

class EmailControllerSpec extends SpecBase {

  "showUnverified" must {
    "return unverified email" in new Setup {

      when(requestBuilder.execute(any[HttpReads[EmailUnverifiedResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(response))

      when(mockHttpClient.get(any)(any)).thenReturn(requestBuilder)

      running(application) {
        val connector = application.injector.instanceOf[DataStoreConnector]

        val result: Future[Option[String]] = connector.retrieveUnverifiedEmail(hc)
        await(result) mustBe expectedResult
      }
    }

    "return unverified email response" in new Setup {
      when(requestBuilder.execute(any[HttpReads[EmailUnverifiedResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(response))

      when(mockHttpClient.get(any)(any)).thenReturn(requestBuilder)

      running(application) {
        val request = fakeRequest(GET, routes.EmailController.showUnverified().url)
        val result  = route(application, request).value
        status(result) shouldBe OK
      }
    }
  }

  "showUndeliverable" must {
    "display undelivered email page" in new Setup {

      val verifiedResponse: EmailVerifiedResponse = EmailVerifiedResponse(Some("test@test.com"))

      when(requestBuilder.execute(any[HttpReads[EmailVerifiedResponse]], any[ExecutionContext]))
        .thenReturn(Future.successful(verifiedResponse))

      when(mockHttpClient.get(any)(any)).thenReturn(requestBuilder)

      running(application) {
        val request = fakeRequest(GET, routes.EmailController.showUndeliverable().url)
        val result  = route(application, request).value
        status(result) shouldBe OK
      }
    }
  }

  trait Setup {
    val expectedResult: Option[String] = Some("unverifiedEmail")

    val response: EmailUnverifiedResponse = EmailUnverifiedResponse(Some("unverifiedEmail"))

    val application: Application = applicationBuilder
      .overrides(
        inject.bind[HttpClientV2].toInstance(mockHttpClient)
      )
      .build()
  }
}
