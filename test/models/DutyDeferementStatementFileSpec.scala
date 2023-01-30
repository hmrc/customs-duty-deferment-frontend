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

import connectors.SDESConnector
import models.DDStatementType.Weekly
import models.FileRole.DutyDefermentStatement
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import util.SpecBase
import scala.concurrent.Future
import services.{AuditingService, DocumentService}

class DutyDeferementStatementFileSpec extends SpecBase {

    "getDutyDefermentStatements" should {
      "compare file returns success response when the same" in new Setup {
        val result = currentFile.compare(currentFile)
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

          val differentFile: DutyDefermentStatementFile =
            DutyDefermentStatementFile(
              "", "", 0L,
              DutyDefermentStatementFileMetadata(
                2019, 1, 1, 2020, 1, 1, FileFormat.Csv,
                DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
            )

          val result = currentFile.compare(differentFile)
          result mustBe 0
        }

        "compare file returns FileFormatUnknown when unknwon file format is found" in new Setup {

          val differentFile: DutyDefermentStatementFile =
            DutyDefermentStatementFile(
              "", "", 0L,
              DutyDefermentStatementFileMetadata(
                2019, 1, 1, 2020, 1, 1, FileFormat.UnknownFileFormat,
                DutyDefermentStatement, Weekly, Some(true), Some(""), "", None)
            )

            val result = currentFile.compare(differentFile)
            result mustBe -1
          }
      }

      trait Setup {
        val mockSDESConnector: SDESConnector = mock[SDESConnector]
        val mockAuditingService: AuditingService = mock[AuditingService]

        implicit val hc: HeaderCarrier = HeaderCarrier()

        val currentFile: DutyDefermentStatementFile =
          DutyDefermentStatementFile(
            "someFilename",
            "downloadUrl",
            10L,
            DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv,
              DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", None)
          )

        val requestedFile: DutyDefermentStatementFile =
          DutyDefermentStatementFile(
            "someRequestedFilename",
            "downloadUrl",
            10L,
            DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv,
              DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", Some("requestedId"))
          )

        val app: Application = application().overrides(
          inject.bind[AuditingService].toInstance(mockAuditingService),
          inject.bind[SDESConnector].toInstance(mockSDESConnector)
        ).build()

        val service: DocumentService = app.injector.instanceOf[DocumentService]
      }
}
