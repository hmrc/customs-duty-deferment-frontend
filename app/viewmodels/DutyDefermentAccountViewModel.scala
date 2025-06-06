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
import uk.gov.hmrc.govukfrontend.views.html.components.GovukAccordion

import utils.ViewUtils._
import views.html.requested_statements
import views.html.components.duty_deferment_accordian
import views.html.duty_deferment_account.duty_deferment_head

case class DDHeadWithEntry(ddHead: HtmlFormat.Appendable, entries: HtmlFormat.Appendable)

case class DDHeadWithEntriesOrNoStatements(
  ddHeadWithEntry: Option[DDHeadWithEntry] = None,
  noStatementsMsg: Option[HtmlFormat.Appendable] = None
)

case class FirstPopulatedStatement(
  historicEoriHeading: Option[HtmlFormat.Appendable] = None,
  ddHeadWithEntriesOrNoStatements: DDHeadWithEntriesOrNoStatements
)

case class TailingStatement(
  historicEoriHeadingMsg: Option[HtmlFormat.Appendable] = None,
  accordian: HtmlFormat.Appendable
)

case class CurrentStatementRow(
  firstPopulatedStatements: Option[FirstPopulatedStatement] = None,
  tailingStatements: Seq[TailingStatement] = Seq(),
  noStatementMsg: Option[HtmlFormat.Appendable] = None
)

case class GuidanceRow(
  h2Heading: HtmlFormat.Appendable,
  link: Option[HtmlFormat.Appendable] = None,
  paragraph: Option[HtmlFormat.Appendable] = None
)

case class DutyDefermentAccountViewModel(
  accountNumberMsg: HtmlFormat.Appendable,
  ddStatementHeading: HtmlFormat.Appendable,
  directDebitInfoMsg: HtmlFormat.Appendable,
  requestedStatement: Option[HtmlFormat.Appendable] = None,
  currentStatements: CurrentStatementRow,
  statOlderThanSixMonths: GuidanceRow,
  chiefDeclaration: GuidanceRow,
  helpAndSupport: GuidanceRow
)

object DutyDefermentAccountViewModel {

  def apply(
    accountNumber: String,
    statementsForAllEoris: Seq[DutyDefermentStatementsForEori],
    linkId: String,
    isNiAccount: Boolean,
    serviceUnavailableUrl: String
  )(implicit appConfig: AppConfig, messages: Messages): DutyDefermentAccountViewModel = {

    val hasRequestedStatements: Boolean = statementsForAllEoris.exists(_.requestedStatements.nonEmpty)
    val hasCurrentStatements: Boolean   = statementsForAllEoris.exists(_.currentStatements.nonEmpty)

    def firstPopulatedStatement: Option[DutyDefermentStatementsForEori] = statementsForAllEoris.find(_.groups.nonEmpty)

    def tailingStatements: Seq[DutyDefermentStatementsForEori] =
      firstPopulatedStatement.fold(Seq.empty[DutyDefermentStatementsForEori])(value =>
        statementsForAllEoris.filterNot(_ == value)
      )

    DutyDefermentAccountViewModel(
      accountNumberMsg = accountNumberMsg(accountNumber, isNiAccount),
      ddStatementHeading = ddStatementHeadingMsg,
      directDebitInfoMsg = directDebitMessage,
      requestedStatement = requestedStatements(linkId, hasRequestedStatements),
      currentStatements =
        currentStatements(accountNumber, hasCurrentStatements, firstPopulatedStatement, tailingStatements),
      statOlderThanSixMonths = statOlderThanSixMonths(serviceUnavailableUrl),
      chiefDeclaration = chiefDeclaration,
      helpAndSupport = helpAndSupport
    )
  }

  private def accountNumberMsg(accountNumber: String, isNiAccount: Boolean)(implicit
    messages: Messages
  ): HtmlFormat.Appendable = {

    val accNumberMsgKey = if (isNiAccount) "cf.account.NiAccount" else "cf.account-number"

    captionComponent(messages(accNumberMsgKey, accountNumber), Some("eori-heading"), "govuk-caption-xl")
  }

  private def ddStatementHeadingMsg(implicit messages: Messages): HtmlFormat.Appendable =
    h1Component(msgKey = "cf.account.detail.deferment-account-heading", id = Some("statements-heading"))

  private def directDebitMessage(implicit messages: Messages): HtmlFormat.Appendable =
    pComponent(
      id = Some("direct-debit-info"),
      content = Html(messages("cf.account.detail.direct-debit.duty-vat-and-excise"))
    )

  private def requestedStatements(linkId: String, hasRequestedStatements: Boolean)(implicit
    appConfig: AppConfig,
    messages: Messages
  ): Option[HtmlFormat.Appendable] =
    if (hasRequestedStatements) Some(new requested_statements(emptyLinkComponent).apply(linkId)) else None

  private def currentStatements(
    accountNumber: String,
    hasCurrentStatements: Boolean,
    firstPopulatedStatements: Option[DutyDefermentStatementsForEori],
    tailingStatements: Seq[DutyDefermentStatementsForEori]
  )(implicit messages: Messages): CurrentStatementRow =
    if (hasCurrentStatements) {
      CurrentStatementRow(
        firstPopulatedStatements = populatedStatements(firstPopulatedStatements, accountNumber),
        tailingStatements = prepareTailingStatements(tailingStatements)
      )
    } else {
      CurrentStatementRow(noStatementMsg =
        Some(insetComponent(msg = messages("cf.account.detail.no-statements", accountNumber)))
      )
    }

  private def populatedStatements(statements: Option[DutyDefermentStatementsForEori], accountNumber: String)(implicit
    messages: Messages
  ): Option[FirstPopulatedStatement] = {

    val headWithNoStatement = DDHeadWithEntriesOrNoStatements(
      noStatementsMsg = Some(insetComponent(msg = messages("cf.account.detail.no-statements", accountNumber)))
    )

    statements.fold[Option[FirstPopulatedStatement]] {
      Some(FirstPopulatedStatement(None, headWithNoStatement))
    } { statement =>
      val historicEoriHeading: Option[HtmlFormat.Appendable] = if (statement.eoriHistory.isHistoricEori) {
        Some(
          h2Component(
            id = Some("historic-eori-0"),
            msg = messages("cf.account.details.previous-eori", statement.eoriHistory.eori)
          )
        )
      } else {
        None
      }

      val headWithEntriesOrNoStatement = if (statement.groups.nonEmpty) {
        val ddHead = new duty_deferment_head(emptyH2Component, emptyPComponent).apply(statement.groups.head)

        val entries: HtmlFormat.Appendable =
          new duty_deferment_accordian(new GovukAccordion()).apply(statement.groups.tail, 0)

        DDHeadWithEntriesOrNoStatements(ddHeadWithEntry = Some(DDHeadWithEntry(ddHead, entries)))
      } else {
        headWithNoStatement
      }

      Some(FirstPopulatedStatement(historicEoriHeading, headWithEntriesOrNoStatement))
    }
  }

  private def prepareTailingStatements(statements: Seq[DutyDefermentStatementsForEori])(implicit
    messages: Messages
  ): Seq[TailingStatement] =
    statements.zipWithIndex.map { statement =>
      val statementsForEori = statement._1
      val historyIndex      = statement._2

      val historicEoriHeading =
        if (statementsForEori.eoriHistory.isHistoricEori && statementsForEori.currentStatements.nonEmpty) {
          Some(
            h2Component(
              id = Some(s"historic-eori-${historyIndex + 1}"),
              msg = messages("cf.account.details.previous-eori", statementsForEori.eoriHistory.eori)
            )
          )
        } else {
          None
        }

      val accordian = new duty_deferment_accordian(new GovukAccordion())
        .apply(statementsForEori.groups, historyIndex + 1)

      TailingStatement(historicEoriHeading, accordian)
    }

  private def statOlderThanSixMonths(serviceUnavailableUrl: String)(implicit messages: Messages): GuidanceRow =
    GuidanceRow(
      h2Heading = h2Component(
        id = Some("missing-documents-guidance-heading"),
        msg = messages("cf.common.missing-documents-guidance.cdsStatements.heading")
      ),
      link = Some(
        linkComponent(
          LinkComponentValues(
            pId = Some("missing-documents-guidance-text1"),
            linkMessageKey = "cf.accounts.older-statements.description.link",
            location = serviceUnavailableUrl,
            linkClass = "govuk-link govuk-link--no-visited-state",
            preLinkMessageKey = Some("cf.accounts.older-statements.description"),
            linkSentence = true
          )
        )
      )
    )

  private def chiefDeclaration(implicit appConfig: AppConfig, messages: Messages): GuidanceRow =
    GuidanceRow(
      h2Heading = h2Component(
        id = Some("chief-guidance-heading"),
        msg = messages("cf.common.chiefStatements.heading"),
        h2Class = Some("govuk-!-margin-top-6")
      ),
      paragraph = Some(
        pComponent(
          id = Some("chief-documents-guidance-text1"),
          classes = Some("govuk-body govuk-!-margin-bottom-7"),
          content = Html(messages("cf.accounts.chiefStatements.description")),
          tabLink = Some(
            hmrcNewTabLinkComponent(
              classList = Some("govuk-link govuk-link--no-visited-state"),
              href = Some(appConfig.chiefDDstatementsLink),
              text = messages("cf.accounts.chiefStatements.description.link")
            )
          )
        )
      )
    )

  private def helpAndSupport(implicit appConfig: AppConfig, messages: Messages): GuidanceRow =
    GuidanceRow(
      h2Heading = h2Component(
        id = Some("dd-support-message-heading"),
        msg = messages("cf.accounts.support.heading")
      ),
      paragraph = Some(
        pComponent(
          id = Some("dd-support-message"),
          classes = Some("govuk-body govuk-!-margin-bottom-9"),
          content = Html(messages("cf.accounts.support.message")),
          tabLink = Some(
            hmrcNewTabLinkComponent(
              classList = Some("govuk-link"),
              href = Some(appConfig.ddAccountSupportLink),
              text = messages("cf.account.dd.support.link")
            )
          )
        )
      )
    )

}
