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

package models.responses

import util.SpecBase
import play.api.libs.json.{JsSuccess, Json}

class EmailResponseSpec extends SpecBase {
  "Json format" should {

    "generate correct output for Json Reads" in new Setup {
      import EmailResponse.format

      Json.fromJson(Json.parse(emailResJsString)) mustBe JsSuccess(emailResOb)

      Json.fromJson(Json.parse(emailResWithoutTSJsString)) mustBe JsSuccess(emailResOb.copy(timestamp = None))
      Json.fromJson(Json.parse(emailResWithoutAddressJsString)) mustBe JsSuccess(emailResOb.copy(address = None))
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(emailResOb) mustBe Json.parse(emailResJsString)
    }
  }

  trait Setup {

    val emailResOb: EmailResponse =
      EmailResponse(address = Some("some@email.com"), timestamp = Some("2023-12-04T16:17:25"))

    val emailResJsString: String =
      """{
        |"address":"some@email.com",
        |"timestamp":"2023-12-04T16:17:25"
        |}""".stripMargin

    val emailResWithoutTSJsString: String =
      """{
        |"address":"some@email.com"
        |}""".stripMargin

    val emailResWithoutAddressJsString: String =
      """{
        |"timestamp":"2023-12-04T16:17:25"
        |}""".stripMargin
  }
}
