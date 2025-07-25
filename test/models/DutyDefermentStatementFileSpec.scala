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

import controllers.routes
import models.DDStatementType.{DutyDeferment, Excise, ExciseDeferment, Supplementary, Weekly}
import models.FileRole.DutyDefermentStatement
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class DutyDefermentStatementFileSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "compare file returns success response when the same" in new Setup {
      val result: Int = currentFile.compare(currentFile)
      result mustBe 0
    }

    "compare file returns failure response when start month is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile(
          "",
          "",
          0L,
          DutyDefermentStatementFileMetadata(
            0,
            0,
            0,
            0,
            0,
            0,
            FileFormat.Csv,
            DutyDefermentStatement,
            Weekly,
            Some(true),
            Some(""),
            "",
            None
          )
        )
      }
    }

    "compare file returns failure response when end month is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile(
          "",
          "",
          0L,
          DutyDefermentStatementFileMetadata(
            0,
            1,
            1,
            0,
            0,
            1,
            FileFormat.Csv,
            DutyDefermentStatement,
            Weekly,
            Some(true),
            Some(""),
            "",
            None
          )
        )
      }
    }

    "compare file returns failure response when start day is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile(
          "",
          "",
          0L,
          DutyDefermentStatementFileMetadata(
            0,
            1,
            0,
            0,
            0,
            0,
            FileFormat.Csv,
            DutyDefermentStatement,
            Weekly,
            Some(true),
            Some(""),
            "",
            None
          )
        )
      }
    }

    "compare file returns failure response when end day is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile(
          "",
          "",
          0L,
          DutyDefermentStatementFileMetadata(
            0,
            1,
            1,
            0,
            1,
            0,
            FileFormat.Csv,
            DutyDefermentStatement,
            Weekly,
            Some(true),
            Some(""),
            "",
            None
          )
        )
      }
    }

    "compare file returns success when all dates are valid" in new Setup {

      val periodStartYear = 2019
      val periodEndYear   = 2020

      val differentFile: DutyDefermentStatementFile =
        DutyDefermentStatementFile(
          "",
          "",
          0L,
          DutyDefermentStatementFileMetadata(
            periodStartYear,
            1,
            1,
            periodEndYear,
            1,
            1,
            FileFormat.Csv,
            DutyDefermentStatement,
            Weekly,
            Some(true),
            Some(""),
            "",
            None
          )
        )

      val result: Int = currentFile.compare(differentFile)
      result mustBe 0
    }

    "compare file returns FileFormatUnknown when unknown file format is found" in new Setup {

      val periodStartYear = 2019
      val periodEndYear   = 2020

      val differentFile: DutyDefermentStatementFile =
        DutyDefermentStatementFile(
          "",
          "",
          0L,
          DutyDefermentStatementFileMetadata(
            periodStartYear,
            1,
            1,
            periodEndYear,
            1,
            1,
            FileFormat.UnknownFileFormat,
            DutyDefermentStatement,
            Weekly,
            Some(true),
            Some(""),
            "",
            None
          )
        )

      val result: Int = currentFile.compare(differentFile)
      result mustBe -1
    }
  }

  "downloadLinkAriaLabel" should {

    "return a correct label for Supplementary statementType" in new Setup {
      val label = currentSupplementaryFile.downloadLinkAriaLabel()(messages)
      label.contains("supplementary") mustBe true
      label.contains("excise") mustBe false
    }

    "return a correct label for Excise statementType" in new Setup {
      val label = currentExciseFile.downloadLinkAriaLabel()(messages)
      label.contains("excise") mustBe true
      label.contains("supplementary") mustBe false
    }

    "return a correct label for ExciseDeferment statementType" in new Setup {
      val label = currentExciseDefermentFile.downloadLinkAriaLabel()(messages)
      label.contains("excise deferment") mustBe true
      label.contains("supplementary") mustBe false
    }

    "return a correct label for DutyDeferment statementType" in new Setup {
      val label = currentDutyDefermentFile.downloadLinkAriaLabel()(messages)
      label.contains("duty deferment") mustBe true
      label.contains("excise deferment") mustBe false
    }
  }

  "DutyDefermentStatementFile.format" should {
    "generate correct output for Json Reads" in new Setup {

      import DutyDefermentStatementFile.format

      Json.fromJson(Json.parse(ddStatFileJsString)) mustBe JsSuccess(currentDutyDefermentFile)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(currentDutyDefermentFile) mustBe Json.parse(ddStatFileJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"filename\": \"test\", \"downloadURL\": \"test_url\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[DutyDefermentStatementFile]
      }
    }
  }

  trait Setup {
    val fileSize = 10L

    val currentFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          Weekly,
          Some(true),
          Some("BACS"),
          someDan,
          None
        )
      )

    val currentSupplementaryFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          Supplementary,
          Some(true),
          Some("BACS"),
          someDan,
          None
        )
      )

    val currentExciseFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          Excise,
          Some(true),
          Some("BACS"),
          someDan,
          None
        )
      )

    val currentExciseDefermentFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          ExciseDeferment,
          Some(true),
          Some("BACS"),
          someDan,
          None
        )
      )

    val currentDutyDefermentFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          DutyDeferment,
          Some(true),
          Some("BACS"),
          someDan,
          None
        )
      )

    val ddStatFileJsString: String =
      """{"filename":"someFilename",
        |"downloadURL":"downloadUrl",
        |"fileSize":10,
        |"metadata":{"periodStartYear":2023,
        |"periodStartMonth":10,
        |"periodStartDay":1,
        |"periodEndYear":2023,
        |"periodEndMonth":10,
        |"periodEndDay":8,
        |"fileFormat":"CSV",
        |"fileRole":"DutyDefermentStatement",
        |"defermentStatementType":"DD1720",
        |"dutyOverLimit":true,
        |"dutyPaymentType":"BACS",
        |"dan":"123456"
        |}
        |}""".stripMargin

    val request: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url)
        .withHeaders("X-Session-Id" -> "someSessionId")
  }
}
