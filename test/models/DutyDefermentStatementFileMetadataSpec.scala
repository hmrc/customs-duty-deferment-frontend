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

import play.api.libs.json.{JsResult, JsValue, Json}
import util.SpecBase

class DutyDefermentStatementFileMetadataSpec extends SpecBase {

  "DutyDefermentStatementFileMetadata" should {
    "serialize to JSON and deserialize back correctly" in new Setup {

      parsedMetadata.isSuccess mustBe true
      parsedMetadata.get mustEqual metadata
    }
  }

  "convert to Map correctly" in new Setup {

    metadata.toMap mustBe expectedMap
  }

  trait Setup {

    val metadata: DutyDefermentStatementFileMetadata = DutyDefermentStatementFileMetadata(
      periodStartYear = 2023,
      periodStartMonth = 10,
      periodStartDay = 1,
      periodEndYear = 2023,
      periodEndMonth = 10,
      periodEndDay = 31,
      fileFormat = FileFormat.Csv,
      fileRole = FileRole.DutyDefermentStatement,
      defermentStatementType = DDStatementType.Weekly,
      dutyOverLimit = Some(true),
      dutyPaymentType = Some("BACS"),
      dan = "DAN123456",
      statementRequestId = Some("123456")
    )

    val expectedMap = Map(
      "periodEndYear" -> "2023",
      "defermentStatementType" -> "Weekly",
      "dan" -> "DAN123456",
      "fileRole" -> "DutyDefermentStatement",
      "dutyPaymentType" -> "Some(BACS)",
      "periodEndDay" -> "31",
      "dutyOverLimit" -> "Some(true)",
      "periodStartDay" -> "1",
      "fileFormat" -> "CSV",
      "periodStartMonth" -> "10",
      "periodStartYear" -> "2023",
      "statementRequestId" -> "Some(123456)",
      "periodEndMonth" -> "10"
    )

    val json: JsValue = Json.toJson(metadata)

    val parsedMetadata: JsResult[DutyDefermentStatementFileMetadata] = Json.fromJson[DutyDefermentStatementFileMetadata](json)

  }
}
