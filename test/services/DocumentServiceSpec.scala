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

package services

import connectors.SDESConnector
import models.DDStatementType.Weekly
import models.FileRole.DutyDefermentStatement
import models.{DutyDefermentStatementFile, DutyDefermentStatementFileMetadata, EoriHistory, FileFormat}
import play.api.{Application, inject}
import util.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DocumentServiceSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "generate correct output" in new Setup {
      when(mockSDESConnector.getDutyDefermentStatements(any, any)(any))
        .thenReturn(Future.successful(dutyDefermentStatementFiles))

      service.getDutyDefermentStatements(eoriHistory, someDan).map { ddStatements =>
        ddStatements mustBe dutyDefermentStatementsForEori01.copy(requestedStatements = Seq())
      }
    }
  }

  trait Setup {
    val mockSDESConnector: SDESConnector     = mock[SDESConnector]
    val mockAuditingService: AuditingService = mock[AuditingService]

    val eoriHist: EoriHistory = EoriHistory("GB123456789", None, None)

    val startYear  = previousMonthDate.getYear
    val startMonth = previousMonthDate.getMonthValue
    val startDate  = 1
    val endYear    = previousMonthDate.getYear
    val endMonth   = previousMonthDate.getMonthValue
    val endDate    = 8
    val fileSize   = 10L

    val currentFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          startYear,
          startMonth,
          startDate,
          endYear,
          endMonth,
          endDate,
          FileFormat.Csv,
          DutyDefermentStatement,
          Weekly,
          Some(true),
          Some("BACS"),
          "123456",
          None
        )
      )

    val requestedFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someRequestedFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          startYear,
          startMonth,
          startDate,
          endYear,
          endMonth,
          endDate,
          FileFormat.Csv,
          DutyDefermentStatement,
          Weekly,
          Some(true),
          Some("BACS"),
          "123456",
          Some("requestedId")
        )
      )

    implicit val application: Application = applicationBuilder
      .overrides(
        inject.bind[AuditingService].toInstance(mockAuditingService),
        inject.bind[SDESConnector].toInstance(mockSDESConnector)
      )
      .build()

    val service: DocumentService = instanceOf[DocumentService]
  }
}
