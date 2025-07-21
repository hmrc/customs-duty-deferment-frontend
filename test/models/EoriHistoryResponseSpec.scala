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

import play.api.libs.json.{JsResultException, JsSuccess, Json}
import util.SpecBase

import java.time.LocalDate

class EoriHistoryResponseSpec extends SpecBase {

  "Json Reads" should {

    "generate correct output" in new Setup {
      import EoriHistoryResponse.format

      Json.fromJson(Json.parse(eoriHistoryResObJsString)) mustBe JsSuccess(eoriHistoryResOb)
    }
  }

  "Json Writes" should {

    "generate correct output" in new Setup {
      Json.toJson(eoriHistoryResOb) mustBe Json.parse(eoriHistoryResObJsString)
    }
  }

  "Invalid JSON" should {
    "fail" in {
      val invalidJson = "{ \"eori1\": \"testEori1\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[EoriHistoryResponse]
      }
    }
  }

  trait Setup {
    val eoriHistoryResOb: EoriHistoryResponse = EoriHistoryResponse(
      Seq(
        EoriHistory(
          eori = validEori,
          validFrom = Some(LocalDate.of(year2023, month1, day10)),
          validUntil = Some(LocalDate.of(year2023, month1, day10))
        )
      )
    )

    val eoriHistoryResObJsString: String =
      """{
        |"eoriHistory":[{"eori":"someEori","validFrom":"2023-01-10","validUntil":"2023-01-10"}]
        |}""".stripMargin
  }
}
