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

import models.DDStatementType.{DutyDeferment, Excise, ExciseDeferment, Supplementary, Weekly}
import models.{DutyDefermentStatementFile, DutyDefermentStatementFileMetadata, FileFormat}
import models.FileRole.DutyDefermentStatement
import util.SpecBase
import org.scalatest.matchers.should.Matchers.shouldBe
import utils.Utils.listOfPastNthMonths

import java.time.LocalDate

class DutyDefermentStatementForEoriSpec extends SpecBase {

  "groups" should {

    "month handling" should {
      "always return current month plus at least 6 months previous for partial data" in new Setup {
        override val currentStatements: Seq[DutyDefermentStatementFile] = dutyDefermentStatementFiles

        result.groups.size shouldBe 7
      }
      "always return current month plus at least 6 months previous for no data" in new Setup {

        result.groups.size shouldBe 7
      }
      "return 8th month when statements are present for 8th month (catching 217 day retention)" in new Setup {
        override val currentStatements: Seq[DutyDefermentStatementFile] = List(ddStatementFile(metadata07))

        result.groups.size       shouldBe 8
        result.groups.last.month shouldBe todaysDate.minusMonths(7).getMonth.getValue
      }
      "remove 7th month when statements are NOT present for 7th month" in new Setup {

        result.groups.size       shouldBe 7
        result.groups.last.month shouldBe todaysDate.minusMonths(6).getMonth.getValue
      }
      "return months in descending order (latest first)" in new Setup {

        val months: Seq[LocalDate]                = result.groups.map(_.monthAndYear)
        val expectedMonths: IndexedSeq[LocalDate] = listOfPastNthMonths(endDate, 6)

        months shouldBe expectedMonths
      }
      "exclude statements older than 7 months" in new Setup {
        override val currentStatements: Seq[DutyDefermentStatementFile] =
          dutyDefermentStatementFiles ++ List(ddStatementFile(metadata07), ddStatementFile(metaData10))

        result.groups.size shouldBe 8
      }
      "populate missing months with empty dummy periods" in new Setup {

        result.groups.flatMap(_.periods.flatMap(_.statementFiles)) shouldBe empty
      }
    }

    "period grouping" should {
      "group multiple files with identical metadata into one period" in new Setup {
        override val currentStatements: Seq[DutyDefermentStatementFile] =
          List(ddStatementFile(metaData02), ddStatementFile(metaData02))

        val month         = ddStatementFile(metaData02).monthAndYear
        val periods       = result.groups.find(_.monthAndYear == month).value.periods
        val weeklyperiods = periods.filter(_.defermentStatementType == Weekly)

        weeklyperiods.size                                                     shouldBe 4
        weeklyperiods.count(_.periodIssueNumber == 1)                          shouldBe 1
        weeklyperiods.find(_.periodIssueNumber == 1).value.statementFiles.size shouldBe 2
        weeklyperiods.find(_.periodIssueNumber == 2).value.statementFiles      shouldBe empty
      }
      "keep separate periods when metadata differs e.g. same month but different week statement" in new Setup {
        val metaData02Week2 =
          ddStatementFile(metaData02.copy(periodStartDay = 8, periodEndDay = 15, periodIssueNumber = 2))

        override val currentStatements: Seq[DutyDefermentStatementFile] =
          List(ddStatementFile(metaData02), ddStatementFile(metaData02), metaData02Week2)

        val month         = ddStatementFile(metaData02).monthAndYear
        val periods       = result.groups.find(_.monthAndYear == month).value.periods
        val weeklyperiods = periods.filter(_.defermentStatementType == Weekly)

        weeklyperiods.size                                                     shouldBe 4
        weeklyperiods.count(_.periodIssueNumber == 1)                          shouldBe 1
        weeklyperiods.find(_.periodIssueNumber == 1).value.statementFiles.size shouldBe 2
        weeklyperiods.find(_.periodIssueNumber == 2).value.statementFiles.size shouldBe 1
      }
    }

    "weekly logic"     should {
      "include all existing weekly periods and add dummy periods for those missing" in new Setup {
        override val currentStatements: Seq[DutyDefermentStatementFile] =
          List(ddStatementFile(metaData02))

        val month         = ddStatementFile(metaData02).monthAndYear
        val periods       = result.groups.find(_.monthAndYear == month).value.periods
        val weeklyperiods = periods.filter(_.defermentStatementType == Weekly)

        weeklyperiods.size                                                     shouldBe 4
        weeklyperiods.count(_.periodIssueNumber == 1)                          shouldBe 1
        weeklyperiods.count(_.periodIssueNumber == 2)                          shouldBe 1
        weeklyperiods.count(_.periodIssueNumber == 3)                          shouldBe 1
        weeklyperiods.count(_.periodIssueNumber == 4)                          shouldBe 1
        weeklyperiods.find(_.periodIssueNumber == 1).value.statementFiles.size shouldBe 1
        weeklyperiods.find(_.periodIssueNumber == 2).value.statementFiles      shouldBe empty
        weeklyperiods.find(_.periodIssueNumber == 3).value.statementFiles      shouldBe empty
        weeklyperiods.find(_.periodIssueNumber == 4).value.statementFiles      shouldBe empty
      }
    }
    "non-weekly logic" should {
      "include all existing non weekly periods and add dummy periods for those missing" in new Setup {
        val metaData02Week2 = ddStatementFile(metaData02.copy(defermentStatementType = Excise))

        override val currentStatements: Seq[DutyDefermentStatementFile] = List(metaData02Week2)

        val month   = ddStatementFile(metaData02).monthAndYear
        val periods = result.groups.find(_.monthAndYear == month).value.periods

        periods.find(_.defermentStatementType == Excise).value.statementFiles.size     shouldBe 1
        periods.find(_.defermentStatementType == Supplementary).value.statementFiles   shouldBe empty
        periods.find(_.defermentStatementType == ExciseDeferment).value.statementFiles shouldBe empty
        periods.find(_.defermentStatementType == DutyDeferment).value.statementFiles   shouldBe empty
        periods.filter(_.defermentStatementType == Weekly).size                        shouldBe 4

      }
    }
    "cutoff behaviour" should {
      "not add missing periods before cutoff date" in new Setup {
        override val endDate = todaysDate.withDayOfMonth(1)

        val group = result.groups.find(_.monthAndYear == endDate).value
        group.periods shouldBe empty
      }
      "add missing periods after cutoff date" in new Setup {
        override val currentStatements: Seq[DutyDefermentStatementFile] = List(ddStatementFile(metaData02))

        val month   = ddStatementFile(metaData02).monthAndYear
        val periods = result.groups.find(_.monthAndYear == month).value.periods

        periods.size shouldBe 8
      }
    }
  }

  trait Setup {

    val endDate = todaysDate

    val currentStatements: Seq[DutyDefermentStatementFile]   = Seq.empty
    val requestedStatements: Seq[DutyDefermentStatementFile] = Seq.empty

    lazy val result = DutyDefermentStatementsForEori(
      eoriHistory,
      currentStatements,
      requestedStatements,
      endDate
    )

    val startDate01 = todaysDate.minusMonths(1)
    val startDate02 = todaysDate.minusMonths(2)
    val startDate10 = todaysDate.minusMonths(10)
    val startDate07 = todaysDate.minusMonths(7)

    val metaData01 = DutyDefermentStatementFileMetadata(
      startDate01.getYear,
      startDate01.getMonthValue,
      1,
      startDate01.getYear,
      startDate01.getMonthValue,
      periodStartDay1,
      periodIssueNumber1,
      FileFormat.Csv,
      DutyDefermentStatement,
      Weekly,
      Some(true),
      Some("BACS"),
      "123456",
      None
    )

    val metaData02 = DutyDefermentStatementFileMetadata(
      startDate02.getYear,
      startDate02.getMonthValue,
      1,
      startDate02.getYear,
      startDate02.getMonthValue,
      periodStartDay1,
      periodIssueNumber1,
      FileFormat.Csv,
      DutyDefermentStatement,
      Weekly,
      Some(true),
      Some("BACS"),
      "123456",
      None
    )

    val metaData10 = DutyDefermentStatementFileMetadata(
      startDate10.getYear,
      startDate10.getMonthValue,
      1,
      startDate10.getYear,
      startDate10.getMonthValue,
      periodStartDay1,
      periodIssueNumber1,
      FileFormat.Csv,
      DutyDefermentStatement,
      Weekly,
      Some(true),
      Some("BACS"),
      "123456",
      None
    )

    def ddStatementFile(metadata: DutyDefermentStatementFileMetadata) =
      DutyDefermentStatementFile("someFilename", "downloadUrl", fileSizeData, metadata)

    val metadata07 = DutyDefermentStatementFileMetadata(
      startDate07.getYear,
      startDate07.getMonthValue,
      periodStartDay1,
      startDate07.getYear,
      startDate07.getMonthValue,
      periodEndDay8,
      periodIssueNumber1,
      FileFormat.Csv,
      DutyDefermentStatement,
      Excise,
      None,
      None,
      "1234",
      None
    )
  }
}
