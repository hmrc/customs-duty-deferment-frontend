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
import models.FileRole.DutyDefermentStatement
import models.{DDStatementType, DutyDefermentStatementFile, EoriHistory}
import utils.DateConverters.OrderedLocalDate
import utils.Utils.{firstDayOfPastNthMonth, isEqualOrAfter, isEqualOrBefore, listOfPastNthMonths}
import utils.OrderedByEoriHistory

import java.time.LocalDate
import scala.util.chaining.scalaUtilChainingOps

case class DutyDefermentStatementsForEori(
  eoriHistory: EoriHistory,
  currentStatements: Seq[DutyDefermentStatementFile],
  requestedStatements: Seq[DutyDefermentStatementFile],
  endDate: LocalDate
) extends OrderedByEoriHistory[DutyDefermentStatementsForEori] {

  private val numberOfMonths: Int                                          = 7
  private val startDate: LocalDate                                         = firstDayOfPastNthMonth(endDate, numberOfMonths)
  private val currentStatementsByPeriod: Seq[DutyDefermentStatementPeriod] = groupByPeriod(currentStatements)

  val groups: Seq[DutyDefermentStatementPeriodsByMonth] =
    filterDates(startDate, endDate, groupByMonthAndYear(currentStatementsByPeriod)).pipe(populateEmptyMonths).map(addEmptyStatements)

  private def groupByPeriod(files: Seq[DutyDefermentStatementFile]): Seq[DutyDefermentStatementPeriod] =
    files
      .groupBy(file => (file.metadata.fileRole, file.startDate, file.endDate, file.metadata.defermentStatementType))
      .map { case (_, periodFiles) =>
        DutyDefermentStatementPeriod(
          periodFiles.head.metadata.fileRole,
          periodFiles.head.metadata.defermentStatementType,
          periodFiles.head.metadata.periodIssueNumber,
          periodFiles.head.monthAndYear,
          periodFiles.head.startDate,
          periodFiles.head.endDate,
          periodFiles.sorted
        )
      }
      .toSeq
      .sorted

  private def filterDates(
    startDate: LocalDate,
    endDate: LocalDate,
    periods: Seq[DutyDefermentStatementPeriodsByMonth]
  ): Seq[DutyDefermentStatementPeriodsByMonth] =
    periods.filter(dds => isEqualOrAfter(dds.monthAndYear, startDate) && isEqualOrBefore(dds.monthAndYear, endDate))

  private def groupByMonthAndYear(
    periods: Seq[DutyDefermentStatementPeriod]
  ): Seq[DutyDefermentStatementPeriodsByMonth] = {
    val monthYearSorted = periods.groupBy(_.monthAndYear).toSeq.sortWith(_._1 > _._1)
    monthYearSorted.map { case (monthAndYear, statementPeriods) =>
      DutyDefermentStatementPeriodsByMonth(
        monthAndYear,
        statementPeriods
          .sortBy(orderPeriods)
      )
    }
  }

  private def addEmptyStatements(month: DutyDefermentStatementPeriodsByMonth): DutyDefermentStatementPeriodsByMonth = {

    val (weeklyPeriods, nonWeeklyPeriods) = month.periods.partition(_.defermentStatementType == Weekly)

    val weeklyByWeek: Map[Int, DutyDefermentStatementPeriod] = weeklyPeriods.flatMap {
      period => period.statementFiles.headOption.map {
        file => val week = file.metadata.periodIssueNumber
          week -> period
      }
    }.toMap

    val completedWeeklyPeriod: Seq[DutyDefermentStatementPeriod] = (1 to 4).map { week =>
       weeklyByWeek.getOrElse(
         week,
         createEmptyPeriod(month.monthAndYear, Weekly, week)
       )
    }

    val expectedWeeklyTypes = Seq(Supplementary, Excise, DutyDeferment, ExciseDeferment)

    val nonWeeklyByType: Map[DDStatementType, DutyDefermentStatementPeriod] = nonWeeklyPeriods.map(period =>
      period.defermentStatementType -> period).toMap

    val completeNonWeekly: Seq[DutyDefermentStatementPeriod] = expectedWeeklyTypes.map { statementType =>
      nonWeeklyByType.getOrElse(
        statementType,
        createEmptyPeriod(month.monthAndYear, statementType, 0)
      )
    }

    month.copy(
      periods =
        (completedWeeklyPeriod ++ completeNonWeekly).sortBy(orderPeriods)
    )
  }

  private def createEmptyPeriod(month: LocalDate, statementType: DDStatementType, issueNumber: Int): DutyDefermentStatementPeriod = {

    DutyDefermentStatementPeriod(
      fileRole = DutyDefermentStatement,
      defermentStatementType = statementType,
      periodIssueNumber = issueNumber,
      monthAndYear = month,
      startDate = month,
      endDate = month,
      statementFiles = Seq.empty

    )
  }

  private def orderPeriods(period: DutyDefermentStatementPeriod): (Int, Int) = {
    val typeOrder = period.defermentStatementType.order

    val weekOrder = if(period.defermentStatementType == Weekly) {
      -period.periodIssueNumber
    } else { 0 }

    (typeOrder, weekOrder)
  }

  private def populateEmptyMonths(statementMonthGroups: Seq[DutyDefermentStatementPeriodsByMonth]): Seq[DutyDefermentStatementPeriodsByMonth] = {
    val existingMonths = statementMonthGroups.map(m => m.monthAndYear -> m).toMap

    listOfPastNthMonths(endDate, numberOfMonths).map { month => existingMonths.getOrElse(
      month,
      DutyDefermentStatementPeriodsByMonth(
        month,
        periods = Seq.empty
      )
    )
    }
  }

}
