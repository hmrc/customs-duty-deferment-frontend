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
import views.html.components.{h2, link, p}

class DutyDefermentAccountViewModelSpec extends SpecBase {

  "apply method" must {

    "populate the model correctly" when {

      "current statements are available" in new Setup {
        implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
        implicit val msgs: Messages = messages(app)

        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(dutyDefermentStatementsForEori),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(viewModel)
        shouldContainDDStatementHeading(app, viewModel)
        shouldContainDirectDebitInfoMsg(app, viewModel)
        shouldNotContainRequestedStatementsMsg(app, viewModel)
        shouldContainNoStatementsAvailableMsg(app, viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(app, viewModel)
        shouldContainHelpAndSupportGuidance(app, viewModel)
      }

      "current statements are unavailable" in new Setup {
        implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
        implicit val msgs: Messages = messages(app)

        val viewModel: DutyDefermentAccountViewModel =
          DutyDefermentAccountViewModel(
            accountNumber = accNumber,
            Seq(),
            linkId,
            isNiAccount = false,
            serviceUnavailableUrl = testServiceUnavailableUrl)

        shouldContainAccountNumberMsg(viewModel)
        shouldContainDDStatementHeading(app, viewModel)
        shouldContainDirectDebitInfoMsg(app, viewModel)
        shouldNotContainRequestedStatementsMsg(app, viewModel)
        shouldContainNoStatementsAvailableMsg(app, viewModel)
        shouldContainStatementOlderThanSixMonthsGuidance(app, viewModel)
        shouldContainChiefStatementGuidance(app, viewModel)
        shouldContainHelpAndSupportGuidance(app, viewModel)
      }
    }
  }

  trait Setup {
    val app: Application = application().build()

    val linkId = "test_link_id"
  }

  private def shouldContainAccountNumberMsg(viewModel: DutyDefermentAccountViewModel): Assertion = {

    viewModel.accountNumberMsg mustBe accNumber
  }

  private def shouldContainDDStatementHeading(app: Application,
                                              viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.ddStatementHeading mustBe messages(app)("cf.account.detail.deferment-account-heading")
  }

  private def shouldContainDirectDebitInfoMsg(app: Application,
                                              viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.directDebitInfoMsg mustBe messages(app)("cf.account.detail.direct-debit.duty-vat-and-excise")
  }

  private def shouldContainRequestedStatementsMsg(app: Application,
                                                  viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.requestedStatement must not be empty
  }

  private def shouldNotContainRequestedStatementsMsg(app: Application,
                                                     viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.requestedStatement mustBe empty
  }

  private def shouldContainNoStatementsAvailableMsg(app: Application,
                                                    viewModel: DutyDefermentAccountViewModel): Assertion = {
    viewModel.currentStatements.noStatementMsg.nonEmpty mustBe true
    viewModel.currentStatements.noStatementMsg mustBe messages(app)("cf.account.detail.no-statements", accNumber)
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

  private def shouldContainChiefStatementGuidance(app: Application,
                                                  viewModel: DutyDefermentAccountViewModel)
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
            )
          )
          )
        )(msgs)))
  }

  private def shouldContainHelpAndSupportGuidance(app: Application,
                                                  viewModel: DutyDefermentAccountViewModel)
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
            )
          )
          )
        )(msgs)))
  }

}
