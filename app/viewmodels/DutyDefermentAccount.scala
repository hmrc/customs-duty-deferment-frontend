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

import java.time.LocalDate

case class DutyDefermentAccount(accountNumber: String,
                                statementsForAllEoris: Seq[DutyDefermentStatementsForEori],
                                linkId: String, isNiAccount: Boolean) {

  val hasRequestedStatements: Boolean = statementsForAllEoris.exists(_.requestedStatements.nonEmpty)
  val hasCurrentStatements: Boolean = statementsForAllEoris.exists(_.currentStatements.nonEmpty)
  private val amtMonthsHistory: Int = 6
  private val monthsLength = 5

  def firstPopulatedStatement: Option[DutyDefermentStatementsForEori] = statementsForAllEoris.find(_.groups.nonEmpty)

  val monthsToDisplay: LocalDate = LocalDate.now().minusMonths(amtMonthsHistory)

  def dropOldMonths(months: Seq[DutyDefermentStatementPeriodsByMonth]): Seq[DutyDefermentStatementPeriodsByMonth] =
    months.dropRight(months.length - monthsLength)

  def tailingStatements: Seq[DutyDefermentStatementsForEori] = firstPopulatedStatement.fold(
    Seq.empty[DutyDefermentStatementsForEori])(value => statementsForAllEoris.filterNot(_ == value))
}
