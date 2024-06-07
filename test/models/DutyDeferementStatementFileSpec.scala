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

import config.AppConfig
import controllers.routes
import models.DDStatementType.{Excise, Supplementary, Weekly}
import models.FileRole.DutyDefermentStatement
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import play.api.Application
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase

class DutyDeferementStatementFileSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "compare file returns success response when the same" in new Setup {
      val result: Int = currentFile.compare(currentFile)
      result mustBe 0
    }

    "compare file returns failure response when start month is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile("", "", 0L,
          DutyDefermentStatementFileMetadata(0, 0, 0, 0, 0, 0, FileFormat.Csv,
            DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
        )
      }
    }

    "compare file returns failure response when end month is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile("", "", 0L,
          DutyDefermentStatementFileMetadata(0, 1, 1, 0, 0, 1, FileFormat.Csv,
            DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
        )
      }
    }

    "compare file returns failure response when start day is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile("", "", 0L,
          DutyDefermentStatementFileMetadata(0, 1, 0, 0, 0, 0, FileFormat.Csv,
            DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
        )
      }
    }

    "compare file returns failure response when end day is 0" in new Setup {
      assertThrows[java.time.DateTimeException] {
        DutyDefermentStatementFile("", "", 0L,
          DutyDefermentStatementFileMetadata(0, 1, 1, 0, 1, 0, FileFormat.Csv,
            DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
        )
      }
    }

    "compare file returns success when all dates are valid" in new Setup {

      val periodStartYear = 2019
      val periodEndYear = 2020

      val differentFile: DutyDefermentStatementFile =
        DutyDefermentStatementFile(
          "", "", 0L,
          DutyDefermentStatementFileMetadata(
            periodStartYear, 1, 1, periodEndYear, 1, 1, FileFormat.Csv,
            DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
        )

      val result: Int = currentFile.compare(differentFile)
      result mustBe 0
    }

    "compare file returns FileFormatUnknown when unknwon file format is found" in new Setup {

      val periodStartYear = 2019
      val periodEndYear = 2020

      val differentFile: DutyDefermentStatementFile =
        DutyDefermentStatementFile(
          "", "", 0L,
          DutyDefermentStatementFileMetadata(
            periodStartYear, 1, 1, periodEndYear, 1, 1, FileFormat.UnknownFileFormat,
            DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
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

    "return a correct label for Weekly statementType" in new Setup {
      val label = currentFile.downloadLinkAriaLabel()(messages)
      label.contains("weekly") mustBe false
      label.contains("excise") mustBe false
      label.contains("supplementary") mustBe false
    }
  }

  trait Setup {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val startYear = 2018
    val startMonth = 6
    val startDate = 1
    val endYear = 2018
    val endMonth = 6
    val endDate = 8
    val fileSize = 10L
    val dan = "123456"

    val currentFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(startYear, startMonth, startDate, endDate, endMonth, endDate, FileFormat.Csv,
          DutyDefermentStatement, Weekly, Some(true), Some("BACS"), dan, None)
      )

    val currentSupplementaryFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(startYear, startMonth, startDate, endYear, endMonth, endDate, FileFormat.Csv,
          DutyDefermentStatement, Supplementary, Some(true), Some("BACS"), dan, None)
      )

    val currentExciseFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(startYear, startMonth, startDate, endYear, endMonth, endDate, FileFormat.Csv,
          DutyDefermentStatement, Excise, Some(true), Some("BACS"), dan, None)
      )

    val request: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url).withHeaders(
        "X-Session-Id" -> "someSessionId")

    val app: Application = application().overrides().build()
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    val messages: Messages = messagesApi.preferred(request)
  }
}
