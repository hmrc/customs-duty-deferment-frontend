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
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.hmrcfrontend.views.html.components.HmrcNewTabLink
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.newtablink.NewTabLink
import utils.Utils.emptyString

import java.time.LocalDate
import views.html.requested_statements
import views.html.components.{h1, h2, link, p, caption}

case class CurrentStatementRow(currentStatements: Seq[String] = Seq(),
                               noStatementMsg: Option[String] = None)

case class GuidanceRow(h2Heading: HtmlFormat.Appendable,
                       link: Option[HtmlFormat.Appendable] = None,
                       paragraph: Option[HtmlFormat.Appendable] = None)

case class DutyDefermentAccountViewModel(accountNumberMsg: HtmlFormat.Appendable,
                                         ddStatementHeading: HtmlFormat.Appendable,
                                         directDebitInfoMsg: HtmlFormat.Appendable,
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
           (implicit appConfig: AppConfig, messages: Messages): DutyDefermentAccountViewModel = {

    val hasRequestedStatements: Boolean = statementsForAllEoris.exists(_.requestedStatements.nonEmpty)
    val hasCurrentStatements: Boolean = statementsForAllEoris.exists(_.currentStatements.nonEmpty)
    val amtMonthsHistory: Int = 6
    val monthsLength = 5

    def firstPopulatedStatement: Option[DutyDefermentStatementsForEori] = statementsForAllEoris.find(_.groups.nonEmpty)

    val monthsToDisplay: LocalDate = LocalDate.now().minusMonths(amtMonthsHistory)

    def dropOldMonths(months: Seq[DutyDefermentStatementPeriodsByMonth]): Seq[DutyDefermentStatementPeriodsByMonth] =
      months.dropRight(months.length - monthsLength)

    def tailingStatements: Seq[DutyDefermentStatementsForEori] = firstPopulatedStatement.fold(
      Seq.empty[DutyDefermentStatementsForEori])(value => statementsForAllEoris.filterNot(_ == value))

    DutyDefermentAccountViewModel(accountNumberMsg = accountNumberMsg(accountNumber, isNiAccount),
      ddStatementHeading = ddStatementHeadingMsg,
      directDebitInfoMsg = directDebitMessage,
      requestedStatement = requestedStatements(linkId, hasRequestedStatements),
      currentStatements = currentStatements(accountNumber, hasCurrentStatements),
      statOlderThanSixMonths = statOlderThanSixMonths(serviceUnavailableUrl),
      chiefDeclaration = chiefDeclaration,
      helpAndSupport = helpAndSupport)
  }

 private def accountNumberMsg(accountNumber: String,
                              isNiAccount: Boolean)(implicit messages: Messages):HtmlFormat.Appendable =
   if(isNiAccount) {
    new caption().apply(messages("cf.account.NiAccount", accountNumber), Some("eori-heading"), "govuk-caption-xl")
 } else {
     new caption().apply(messages("cf.account-number", accountNumber), Some("eori-heading"), "govuk-caption-xl")
 }

  private def directDebitMessage(implicit messages: Messages):HtmlFormat.Appendable = {
    new p().apply(
      id = Some("direct-debit-info"),
      content = Html(messages("cf.account.detail.direct-debit.duty-vat-and-excise")))
  }

  private def ddStatementHeadingMsg(implicit messages: Messages): HtmlFormat.Appendable = {
    new h1().apply(
      msg = messages("cf.account.detail.deferment-account-heading"),
      Some("statements-heading"))
  }

  private def requestedStatements(linkId: String, hasRequestedStatements: Boolean)
                                 (implicit appConfig: AppConfig, messages: Messages): Option[HtmlFormat.Appendable] = {
    if (hasRequestedStatements) {
      Some(new requested_statements(new link()).apply(linkId))
    } else {
      None
    }
  }

  private def currentStatements(accountNumber: String,
                                hasCurrentStatements: Boolean)
                               (implicit messages: Messages): CurrentStatementRow = {
    if (hasCurrentStatements) {
      CurrentStatementRow(Seq(), None)
    } else {
      CurrentStatementRow(Seq(), Some(messages("cf.account.detail.no-statements", accountNumber)))
    }
  }

  private def statOlderThanSixMonths(serviceUnavailableUrl: String)(implicit messages: Messages): GuidanceRow = {
    GuidanceRow(h2Heading = new h2().apply(
      id = Some("missing-documents-guidance-heading"),
      msg = messages("cf.common.missing-documents-guidance.cdsStatements.heading")),

      link = Some(new link().apply(
        pId = Some("missing-documents-guidance-text1"),
        linkMessage = "cf.accounts.older-statements.description.link",
        location = serviceUnavailableUrl,
        linkClass = "govuk-link govuk-link--no-visited-state",
        preLinkMessage = Some("cf.accounts.older-statements.description")
      )))
  }

  private def chiefDeclaration(implicit appConfig: AppConfig,
                               messages: Messages): GuidanceRow = {
    GuidanceRow(h2Heading = new h2().apply(
      id = Some("chief-guidance-heading"),
      msg = messages("cf.common.chiefStatements.heading"),
      h2Class = Some("govuk-!-margin-top-6")),

      paragraph = Some(new p().apply(
        id = Some("chief-documents-guidance-text1"),
        classes = Some("govuk-body govuk-!-margin-bottom-7"),
        content = Html(messages("cf.accounts.chiefStatements.description")),
        tabLink = Some(new HmrcNewTabLink().apply(
          NewTabLink(
            language = Some(messages.lang.toString),
            classList = Some("govuk-link govuk-link--no-visited-state"),
            href = Some(appConfig.chiefDDstatementsLink),
            text = messages("cf.accounts.chiefStatements.description.link")
          )))
      ))
    )
  }

  private def helpAndSupport(implicit appConfig: AppConfig, messages: Messages): GuidanceRow = {
    GuidanceRow(h2Heading = new h2().apply(
      id = Some("dd-support-message-heading"),
      msg = messages("cf.accounts.support.heading")),

      paragraph = Some(new p().apply(
        id = Some("dd-support-message"),
        classes = Some("govuk-body govuk-!-margin-bottom-9"),
        content = Html(messages("cf.accounts.support.message")),
        tabLink = Some(new HmrcNewTabLink().apply(
          NewTabLink(
            language = Some(messages.lang.toString),
            classList = Some("govuk-link"),
            href = Some(appConfig.ddAccountSupportLink),
            text = messages("cf.account.dd.support.link")
          )))
      ))
    )
  }

}
