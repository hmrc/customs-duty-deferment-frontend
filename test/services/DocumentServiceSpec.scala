/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import util.SpecBase

import scala.concurrent.Future

class DocumentServiceSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "return the correct requested and current statements" in new Setup {
      when(mockSDESConnector.getDutyDefermentStatements(any,any)(any))
        .thenReturn(Future.successful(Seq(currentFile, requestedFile)))

      when(mockAuditingService.audit(any)(any))
        .thenReturn(Future.successful(AuditResult.Success))

      running(app) {
        val result = await(service.getDutyDefermentStatements(EoriHistory("someEori", None, None), "someDan"))
        result.eoriHistory mustBe EoriHistory("someEori", None, None)
        result.currentStatements mustBe Seq(currentFile)
        result.requestedStatements mustBe Seq(requestedFile)
      }
    }

    "compare file returns success response when the same" in new Setup {
      val result = currentFile.compare(currentFile)
      result mustBe 0
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
        DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", None)
    )

    val requestedFile: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someRequestedFilename",
        "downloadUrl",
        10L,
        DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", Some("requestedId"))
    )

    val app: Application = application().overrides(
      inject.bind[AuditingService].toInstance(mockAuditingService),
      inject.bind[SDESConnector].toInstance(mockSDESConnector)
    ).build()

    val service: DocumentService = app.injector.instanceOf[DocumentService]
  }
}
