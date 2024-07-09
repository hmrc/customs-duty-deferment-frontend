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

package viewmodels

import models.DDStatementType.Weekly
import models.{DutyDefermentStatementFile, DutyDefermentStatementFileMetadata, FileFormat}
import models.FileRole.DutyDefermentStatement
import util.SpecBase
import org.scalatest.matchers.should.Matchers.shouldBe

import java.time.LocalDate

class DutyDefermentStatementForEoriSpec extends SpecBase {

  "groups" must {

    "correctly give statements by month which are less than 7 months" in new Setup {

      val currentStatementsTestData: Seq[DutyDefermentStatementFile] = List(
        DutyDefermentStatementFile("someFilename", "downloadUrl", fileSizeData, metaData01),
        DutyDefermentStatementFile("someFilename2", "downloadUrl", fileSizeData, metaData02),
        DutyDefermentStatementFile("someFilename3", "downloadUrl", fileSizeData, metaData03)
      )

      val requestedStatementsTestData: Seq[DutyDefermentStatementFile] = List(
        DutyDefermentStatementFile("someFilename", "downloadUrl", fileSizeData, metaData01)
      )

      lazy val dutyDefermentStatementsForEori: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
        eoriHistory,
        currentStatementsTestData,
        requestedStatementsTestData,
        LocalDate.now()
      )

      dutyDefermentStatementsForEori.currentStatements.size shouldBe 3
      dutyDefermentStatementsForEori.requestedStatements.size shouldBe 1
      dutyDefermentStatementsForEori.groups.size shouldBe 2
      dutyDefermentStatementsForEori.groups.head.periods.size shouldBe 1
      dutyDefermentStatementsForEori.groups.tail.size shouldBe 1
    }

    "correctly give no statements when dates are more than 7 months" in new Setup {

      val currentStatementsTestData: Seq[DutyDefermentStatementFile] = List(
        DutyDefermentStatementFile("someFilename3", "downloadUrl", fileSizeData, metaData03)
      )

      val requestedStatementsTestData: Seq[DutyDefermentStatementFile] = List(
        DutyDefermentStatementFile("someFilename", "downloadUrl", fileSizeData, metaData01)
      )

      lazy val dutyDefermentStatementsForEori: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
        eoriHistory,
        currentStatementsTestData,
        requestedStatementsTestData,
        LocalDate.now()
      )

      dutyDefermentStatementsForEori.currentStatements.size shouldBe 1
      dutyDefermentStatementsForEori.requestedStatements.size shouldBe 1
      dutyDefermentStatementsForEori.groups.isEmpty shouldBe true
    }

  }

  trait Setup {

    val eightMonths = 8
    val dayTen = 10

    val endDate = LocalDate.now

    val startDate01 = endDate.minusMonths(1)
    val startDate02 = endDate.minusMonths(2)
    val startDate03 = endDate.minusMonths(eightMonths)

    val metaData01 = DutyDefermentStatementFileMetadata(startDate01.getYear, startDate01.getMonthValue, 1,
      startDate01.getYear, startDate01.getMonthValue, dayTen, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true),
      Some("BACS"), "123456", None)

    val metaData02 = DutyDefermentStatementFileMetadata(startDate02.getYear, startDate02.getMonthValue, 1,
      startDate02.getYear, startDate02.getMonthValue, dayTen, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true),
      Some("BACS"), "123456", None)

    val metaData03 = DutyDefermentStatementFileMetadata(startDate03.getYear, startDate03.getMonthValue, 1,
      startDate03.getYear, startDate03.getMonthValue, dayTen, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true),
      Some("BACS"), "123456", None)

  }
}
