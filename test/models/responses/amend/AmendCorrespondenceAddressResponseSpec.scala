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

package models.responses.amend

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class AmendCorrespondenceAddressResponseSpec extends SpecBase {
  "format" should {

    "generate correct output" in new Setup {
      import AmendCorrespondenceAddressResponse.updateResponseFormat

      Json.fromJson(Json.parse(amendCorrespondenceAddressResObJsString)) mustBe JsSuccess(
        amendCorrespondenceAddressResOb
      )
    }

    "Invalid JSON" should {
      "fail" in {
        val invalidJson = "{ \"responseCommon1\": \"pending\"}"

        intercept[JsResultException] {
          Json.parse(invalidJson).as[AmendCorrespondenceAddressResponse]
        }
      }
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(amendCorrespondenceAddressResOb) mustBe Json.parse(amendCorrespondenceAddressResObJsString)
    }

  }

  trait Setup {
    val amendCorrespondenceAddressResObJsString: String =
      """{"responseCommon":{"status":"pending","statusText":"test_status","processingDate":"test_data"}}""".stripMargin

    val responseCommonOb: ResponseCommon = ResponseCommon(
      status = testStatus,
      statusText = Some("test_status"),
      processingDate = "test_data",
      returnParameters = None
    )

    val amendCorrespondenceAddressResOb: AmendCorrespondenceAddressResponse = AmendCorrespondenceAddressResponse(
      responseCommonOb
    )
  }
}
