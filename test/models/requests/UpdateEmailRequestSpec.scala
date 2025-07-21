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

package models.requests

import play.api.libs.json.{JsResultException, JsSuccess, Json}
import util.SpecBase

class UpdateEmailRequestSpec extends SpecBase {
  "Json Reads" should {
    "generate correct output" in new Setup {
      import UpdateEmailRequest.formats

      Json.fromJson(Json.parse(updateEmailRequestJsString)) mustBe JsSuccess(updateEmailRequestOb)
    }
  }

  "Json Writes" should {
    "generate correct output" in new Setup {
      Json.toJson(updateEmailRequestOb) mustBe Json.parse(updateEmailRequestJsString)
    }
  }

  "Invalid JSON" should {
    "fail" in {
      val invalidJson = "{ \"dan\": \"someDan\", \"eori12\": \"thirty\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[UpdateEmailRequest]
      }
    }
  }

  trait Setup {
    val updateEmailRequestOb: UpdateEmailRequest = UpdateEmailRequest(validDan, validEori, emailId)

    val updateEmailRequestJsString: String =
      """{"dan":"someDan","eori":"someEori","email":"test@test.com"}""".stripMargin
  }
}
