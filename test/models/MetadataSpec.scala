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
import play.api.libs.json.{JsSuccess, Json}


class MetadataSpec extends SpecBase {

  "Metadata" should {

    val sampleMetadata: Metadata = Metadata(Seq(
      MetadataItem("PeriodStartYear", "2023"),
      MetadataItem("PeriodStartMonth", "10"),
      MetadataItem("PeriodStartDay", "4"),
      MetadataItem("PeriodEndYear", "2023"),
      MetadataItem("PeriodEndMonth", "10"),
      MetadataItem("PeriodEndDay", "31"),
      MetadataItem("FileType", "CSV"),
      MetadataItem("FileRole", "DutyDefermentStatement"),
      MetadataItem("DefermentStatementType", "DD"),
      MetadataItem("DutyOverLimit", "Y"),
      MetadataItem("DutyPaymentType", "PPD"),
      MetadataItem("DAN", "1234567890"),
      MetadataItem("statementRequestId", "12345")
    ))

    "convert Metadata to DutyDefermentStatementFileMetadata" in {

      val dutyDefermentStatementFileMetadata = sampleMetadata.toDutyDefermentStatementFileMetadata

      assert(dutyDefermentStatementFileMetadata.periodStartYear == 2023)
      assert(dutyDefermentStatementFileMetadata.periodStartMonth == 10)
      assert(dutyDefermentStatementFileMetadata.dutyOverLimit == Some(true))
    }

    "Reads and Writes return corresponding objects" in {

      val jsonVal = Metadata.metadataWrites.writes(sampleMetadata).toString()
      val parsedObj = Metadata.metadataReads.reads(Json.parse(jsonVal))
      parsedObj mustBe JsSuccess(sampleMetadata)
    }
  }

}
