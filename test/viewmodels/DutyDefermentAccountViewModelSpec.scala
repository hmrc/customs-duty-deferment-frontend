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
import org.scalatest.Assertion
import play.api.Application
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.hmrcfrontend.views.html.components.HmrcNewTabLink
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.newtablink.NewTabLink
import util.SpecBase
import views.html.components.{caption, h1, h2, inset, link, p}
import views.html.requested_statements

class DutyDefermentAccountViewModelSpec extends SpecBase {

  "apply method" must {

    "populate the model correctly" when {

      "current statements are available" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori01.copy(requestedStatements = Seq())),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldNotContainRequestedStatementsMsg(viewModel)
        shouldContainCurrentStatementSection(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }

      "current statements with historic eori are available" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori02),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldContainRequestedStatementsMsg(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }

      "current statements are available for tailing statements with historic eori" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori01, dutyDefermentStatementsForEori02),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldContainRequestedStatementsMsg(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }

      "current statements are available for tailing statements without historic eori" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori01, dutyDefermentStatementsForEori03),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldContainRequestedStatementsMsg(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }

      "current statements are available and is a NI account" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori01.copy(requestedStatements = Seq())),
            linkId,
            isNiAccount = true,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel, isNiAccount = true)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldNotContainRequestedStatementsMsg(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }

      "current statements are unavailable" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldNotContainRequestedStatementsMsg(viewModel)
        shouldContainNoStatementsAvailableMsg(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }

      "requested statements are available but current statements are unavailable" in new Setup {
        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori01.copy(currentStatements = Seq())),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(accNumber, viewModel)
        shouldContainDDStatementHeading(viewModel)
        shouldContainDirectDebitInfoMsg(viewModel)
        shouldContainRequestedStatementsMsg(viewModel, linkId)
        shouldContainNoStatementsAvailableMsg(viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(viewModel)
        shouldContainHelpAndSupportGuidance(viewModel)
      }
    }
  }

  private def shouldContainAccountNumberMsg(accountNumber: String,
                                            viewModel: DutyDefermentAccountViewModel,
                                            isNiAccount: Boolean = false)
                                           (implicit messages: Messages): Assertion = {
    if (isNiAccount) {
      viewModel.accountNumberMsg mustBe new caption().apply(
        messages("cf.account.NiAccount", accountNumber), Some("eori-heading"), "govuk-caption-xl")
    } else {
      viewModel.accountNumberMsg mustBe new caption().apply(
        messages("cf.account-number", accountNumber), Some("eori-heading"), "govuk-caption-xl")
    }
  }

  private def shouldContainDDStatementHeading(viewModel: DutyDefermentAccountViewModel)
                                             (implicit msgs: Messages): Assertion = {
    viewModel.ddStatementHeading mustBe new h1()
      .apply(msgs("cf.account.detail.deferment-account-heading"), Some("statements-heading"))
  }

  private def shouldContainDirectDebitInfoMsg(viewModel: DutyDefermentAccountViewModel)
                                             (implicit msgs: Messages): Assertion = {
    viewModel.directDebitInfoMsg mustBe new p()
      .apply(id = Some("direct-debit-info"),
        content = Html(msgs("cf.account.detail.direct-debit.duty-vat-and-excise")))
  }

  private def shouldContainRequestedStatementsMsg(viewModel: DutyDefermentAccountViewModel,
                                                  linkId: String)
                                                 (implicit messages: Messages, appConfig: AppConfig): Assertion = {
    viewModel.requestedStatement mustBe Some(new requested_statements(new link()).apply(linkId))
  }

  private def shouldNotContainRequestedStatementsMsg(viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.requestedStatement mustBe empty
  }

  private def shouldContainRequestedStatementsMsg(viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.requestedStatement.size mustBe 1
  }

  private def shouldContainCurrentStatementSection(viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.currentStatements.noStatementMsg.isEmpty mustBe true
    viewModel.currentStatements.tailingStatements mustBe Seq()

    val headPopulatedSttVal = viewModel.currentStatements.firstPopulatedStatements.get.toString
    headPopulatedSttVal.contains("Excise summary PDF") mustBe true
    headPopulatedSttVal.contains("Download excise summary") mustBe true
    headPopulatedSttVal.contains("Download supplementary") mustBe true
    headPopulatedSttVal.contains("Download 1 to 8") mustBe true
  }

  private def shouldContainNoStatementsAvailableMsg(viewModel: DutyDefermentAccountViewModel)
                                                   (implicit messages: Messages): Assertion = {
    viewModel.currentStatements.noStatementMsg.nonEmpty mustBe true
    viewModel.currentStatements.noStatementMsg mustBe
      Some(new inset().apply(messages("cf.account.detail.no-statements", accNumber)))
  }

  private def shouldContainStatementOlderThanSixMonthsGuidance(app: Application,
                                                               viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.statOlderThanSixMonths mustBe
      GuidanceRow(
        h2Heading = new h2().apply(
          id = Some("missing-documents-guidance-heading"),
          msg = messages(app)("cf.common.missing-documents-guidance.cdsStatements.heading")),

        link = Some(new link().apply(
          pId = Some("missing-documents-guidance-text1"),
          linkMessage = "cf.accounts.older-statements.description.link",
          location = testServiceUnavailableUrl,
          linkClass = "govuk-link govuk-link--no-visited-state",
          preLinkMessage = Some("cf.accounts.older-statements.description"))(messages(app)))
      )
  }

  private def shouldContainChiefStatementGuidance(viewModel: DutyDefermentAccountViewModel)
                                                 (implicit appConfig: AppConfig, msgs: Messages): Assertion = {
    viewModel.chiefDeclaration mustBe
      GuidanceRow(
        h2Heading = new h2().apply(
          id = Some("chief-guidance-heading"),
          msg = msgs("cf.common.chiefStatements.heading"),
          h2Class = Some("govuk-!-margin-top-6")),

        paragraph = Some(new p().apply(
          id = Some("chief-documents-guidance-text1"),
          classes = Some("govuk-body govuk-!-margin-bottom-7"),
          content = Html(msgs("cf.accounts.chiefStatements.description")),
          tabLink = Some(new HmrcNewTabLink().apply(
            NewTabLink(
              language = Some(msgs.lang.toString),
              classList = Some("govuk-link govuk-link--no-visited-state"),
              href = Some(appConfig.chiefDDstatementsLink),
              text = msgs("cf.accounts.chiefStatements.description.link")
            )))
        )(msgs)))
  }

  private def shouldContainHelpAndSupportGuidance(viewModel: DutyDefermentAccountViewModel)
                                                 (implicit appConfig: AppConfig, msgs: Messages): Assertion = {
    viewModel.helpAndSupport mustBe
      GuidanceRow(
        h2Heading = new h2().apply(
          id = Some("dd-support-message-heading"),
          msg = msgs("cf.accounts.support.heading")),

        paragraph = Some(new p().apply(
          id = Some("dd-support-message"),
          classes = Some("govuk-body govuk-!-margin-bottom-9"),
          content = Html(msgs("cf.accounts.support.message")),
          tabLink = Some(new HmrcNewTabLink().apply(
            NewTabLink(
              language = Some(msgs.lang.toString),
              classList = Some("govuk-link"),
              href = Some(appConfig.ddAccountSupportLink),
              text = msgs("cf.account.dd.support.link")
            )))
        )(msgs)))
  }

  trait Setup {
    val app: Application = application().build()
    val linkId = "test_link_id"

    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    implicit val msgs: Messages = messages(app)
  }
}
