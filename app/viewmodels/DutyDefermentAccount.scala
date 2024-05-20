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

import config.AppConfig
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import utils.Utils.emptyString

import java.time.LocalDate
import views.html.requested_statements
import views.html.components.{h2, link, p}

case class CurrentStatementRow(currentStatements: Seq[String] = Seq(),
                               noStatementMsg: Option[String] = None)
case class GuidanceRow(h2Heading: HtmlFormat.Appendable,
                       link: Option[HtmlFormat.Appendable] = None,
                       paragraph: Option[HtmlFormat.Appendable] = None)
case class DutyDefermentAccountViewModel(accountNumberMsg: String,
                                         ddStatementHeading: String,
                                         directDebitInfoMsg: String,
                                         requestedStatement: Option[HtmlFormat.Appendable] = None,
                                         currentStatements: CurrentStatementRow,
                                         statOlderThanSixMonths: GuidanceRow,
                                         chiefDeclaration: GuidanceRow,
                                         helpAndSupport: GuidanceRow)
case class DutyDefermentAccount(accountNumber: String,
                                statementsForAllEoris: Seq[DutyDefermentStatementsForEori],
                                linkId: String,
                                isNiAccount: Boolean) {

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

object DutyDefermentAccountViewModel {

  def apply(accountNumber: String,
            statementsForAllEoris: Seq[DutyDefermentStatementsForEori],
            linkId: String,
            isNiAccount: Boolean,
            serviceUnavailableUrl: String)
           (implicit appConfig: AppConfig, messages: Messages): DutyDefermentAccountViewModel =  {
    DutyDefermentAccountViewModel(
      emptyString,
      emptyString,
      emptyString,
      None,
      CurrentStatementRow(),
      GuidanceRow(h2Heading = new h2().apply(emptyString, None, None)),
      GuidanceRow(h2Heading = new h2().apply(emptyString, None, None)),
      GuidanceRow(h2Heading = new h2().apply(emptyString, None, None))
    )
  }
}
