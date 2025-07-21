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

package models

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class SDDSRequestSpec extends SpecBase {

  "Json Reads" should {
    "generate correct output" in new Setup {
      import SDDSRequest.format

      Json.fromJson(Json.parse(sddsReqJsString)) mustBe JsSuccess(sddsReqOb)
    }
  }

  "Json Writes" should {
    "generate correct output" in new Setup {
      Json.toJson(sddsReqOb) mustBe Json.parse(sddsReqJsString)
    }
  }

  "Invalid JSON" should {
    "fail" in {
      val invalidJson = "{ \"eori\": \"testEori1\", \"isHistoric12\": \"thirty\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[SDDSRequest]
      }
    }
  }

  trait Setup {
    val sddsReqOb: SDDSRequest = SDDSRequest(testLinkUrl, testLinkUrl, validDan, emailId)

    val sddsReqJsString: String =
      """{"returnUrl":"test_url",
        |"backUrl":"test_url",
        |"dan":"someDan",
        |"email":"test@test.com"
        |}""".stripMargin
  }
}
