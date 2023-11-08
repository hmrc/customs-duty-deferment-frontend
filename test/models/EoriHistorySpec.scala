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

import play.api.libs.json.Json
import models.EoriHistory
import util.SpecBase

import java.time.LocalDate

class EoriHistorySpec extends SpecBase {

  "EoriHistory" should {
    "be serializable to JSON" in new Setup {

      val json = Json.toJson(expectedEoriHistory)

      val expectedJson = Json.obj(
        "eori" -> "EORI123",
        "validFrom" -> "2023-01-01",
        "validUntil" -> "2023-12-31"
      )

      json mustEqual expectedJson
    }

    "be deserializable from JSON" in new Setup {
      val json = Json.obj(
        "eori" -> "EORI123",
        "validFrom" -> "2023-01-01",
        "validUntil" -> "2023-12-31"
      )

      val eoriHistory = json.as[EoriHistory]

      eoriHistory mustEqual expectedEoriHistory
    }

    "correctly determine if an EORI is historic" in new Setup {
      val currentEori = EoriHistory("EORI123", Some(LocalDate.now()), None)

      currentEori.isHistoricEori mustBe false
      expectedEoriHistory.isHistoricEori mustBe true
    }
  }

  trait Setup {
    val expectedEoriHistory = EoriHistory(
      "EORI123",
      Some(LocalDate.parse("2023-01-01")),
      Some(LocalDate.parse("2023-12-31"))
    )
  }

}
