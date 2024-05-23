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

package views.duty_deferment_account

import config.AppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import viewmodels.DutyDefermentAccountViewModel
import views.html.duty_deferment_account.duty_deferment_account

class DutyDefermentAccountSpec extends SpecBase {

  "DutyDefermentAccount view" should {

    "display correct title and guidance" in new Setup {
      val viewDoc: Document = view(model)

      viewDoc.title() mustBe
        s"${messages(app)("cf.account.detail.title")} - ${messages(app)("service.name")} - GOV.UK"

      viewDoc.getElementById("eori-heading").text() mustBe messages(app)("cf.account-number", accountNumber)

      viewDoc.getElementById("statements-heading").text() mustBe
        messages(app)("cf.account.detail.deferment-account-heading")

      viewDoc.getElementById("direct-debit-info").text() mustBe
        messages(app)("cf.account.detail.direct-debit.duty-vat-and-excise")

      viewDoc.getElementById("missing-documents-guidance-heading").text() mustBe
        messages(app)("cf.common.missing-documents-guidance.cdsStatements.heading")

      viewDoc.getElementById("chief-guidance-heading").text() mustBe
        messages(app)("cf.common.chiefStatements.heading")

      viewDoc.getElementById("dd-support-message-heading").text() mustBe messages(app)("cf.accounts.support.heading")

      viewDoc.getElementById("missing-documents-guidance-text1").text() must not be empty

      viewDoc.getElementById("chief-documents-guidance-text1").text() must not be empty

      viewDoc.getElementById("request-statement-link").text() must not be empty

      viewDoc.html().contains("cf.accounts.older-statements.description.link")
      viewDoc.html().contains("cf.accounts.older-statements.description")
      viewDoc.html().contains("cf.accounts.chiefStatements.description")
      viewDoc.html().contains(serviceUnavailableUrl)
    }

    "display correct title and guidance when there is no current statements" in new Setup {
      val viewDoc: Document = view(modelWithNoCurrentStatements)

      viewDoc.title() mustBe
        s"${messages(app)("cf.account.detail.title")} - ${messages(app)("service.name")} - GOV.UK"

      viewDoc.getElementById("eori-heading").text() mustBe messages(app)("cf.account-number", accountNumber)

      viewDoc.getElementById("statements-heading").text() mustBe
        messages(app)("cf.account.detail.deferment-account-heading")

      viewDoc.getElementById("direct-debit-info").text() mustBe
        messages(app)("cf.account.detail.direct-debit.duty-vat-and-excise")

      viewDoc.getElementById("missing-documents-guidance-heading").text() mustBe
        messages(app)("cf.common.missing-documents-guidance.cdsStatements.heading")

      viewDoc.getElementById("chief-guidance-heading").text() mustBe
        messages(app)("cf.common.chiefStatements.heading")

      viewDoc.getElementById("dd-support-message-heading").text() mustBe messages(app)("cf.accounts.support.heading")

      viewDoc.getElementById("missing-documents-guidance-text1").text() must not be empty

      viewDoc.getElementById("chief-documents-guidance-text1").text() must not be empty

      viewDoc.getElementById("request-statement-link").text() must not be empty

      viewDoc.html().contains("cf.accounts.older-statements.description.link")
      viewDoc.html().contains("cf.accounts.older-statements.description")
      viewDoc.html().contains("cf.accounts.chiefStatements.description")
      viewDoc.html().contains(msg("cf.account.detail.no-statements", accountNumber))
      viewDoc.html().contains(serviceUnavailableUrl)
    }

    "display correct title and guidance when there is no requested and current statements" in new Setup {
      val viewDoc: Document = view(modelWithNoCurrentAndRequestedStatements)

      viewDoc.title() mustBe
        s"${messages(app)("cf.account.detail.title")} - ${messages(app)("service.name")} - GOV.UK"

      viewDoc.getElementById("eori-heading").text() mustBe messages(app)("cf.account-number", accountNumber)

      viewDoc.getElementById("statements-heading").text() mustBe
        messages(app)("cf.account.detail.deferment-account-heading")

      viewDoc.getElementById("direct-debit-info").text() mustBe
        messages(app)("cf.account.detail.direct-debit.duty-vat-and-excise")

      viewDoc.getElementById("missing-documents-guidance-heading").text() mustBe
        messages(app)("cf.common.missing-documents-guidance.cdsStatements.heading")

      viewDoc.getElementById("chief-guidance-heading").text() mustBe
        messages(app)("cf.common.chiefStatements.heading")

      viewDoc.getElementById("dd-support-message-heading").text() mustBe messages(app)("cf.accounts.support.heading")

      viewDoc.getElementById("missing-documents-guidance-text1").text() must not be empty

      viewDoc.getElementById("chief-documents-guidance-text1").text() must not be empty

      Option(viewDoc.getElementById("request-statement-link")) mustBe empty

      viewDoc.html().contains("cf.accounts.older-statements.description.link")
      viewDoc.html().contains("cf.accounts.older-statements.description")
      viewDoc.html().contains("cf.accounts.chiefStatements.description")
      viewDoc.html().contains(msg("cf.account.detail.no-statements", accountNumber))
      viewDoc.html().contains(serviceUnavailableUrl)
    }
  }

  private def shouldDisplayCorrectCommonGuidanceAndText(viewDoc: Document,
                                                  messages: Messages,
                                                  accountNumber: String,
                                                        serviceUnavailableUrl: String): Assertion = {
    viewDoc.title() mustBe
      s"${messages("cf.account.detail.title")} - ${messages("service.name")} - GOV.UK"

    viewDoc.getElementById("eori-heading").text() mustBe messages("cf.account-number", accountNumber)

    viewDoc.getElementById("statements-heading").text() mustBe
      messages("cf.account.detail.deferment-account-heading")

    viewDoc.getElementById("direct-debit-info").text() mustBe
      messages("cf.account.detail.direct-debit.duty-vat-and-excise")

    viewDoc.getElementById("missing-documents-guidance-heading").text() mustBe
      messages("cf.common.missing-documents-guidance.cdsStatements.heading")

    viewDoc.getElementById("chief-guidance-heading").text() mustBe
      messages("cf.common.chiefStatements.heading")

    viewDoc.getElementById("dd-support-message-heading").text() mustBe messages("cf.accounts.support.heading")

    viewDoc.getElementById("missing-documents-guidance-text1").text() must not be empty

    viewDoc.getElementById("chief-documents-guidance-text1").text() must not be empty

    viewDoc.html().contains("cf.accounts.older-statements.description.link") mustBe true
    viewDoc.html().contains("cf.accounts.older-statements.description") mustBe true
    viewDoc.html().contains("cf.accounts.chiefStatements.description") mustBe true
    viewDoc.html().contains(serviceUnavailableUrl) mustBe true
  }

  trait Setup {
    val app: Application = application().build()
    val serviceUnavailableUrl: String = "service_unavailable_url"
    val accountNumber = "1234567"
    val linkId = "link_id"

    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    implicit val msg: Messages = messages(app)
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val model: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
      accountNumber,
      Seq(dutyDefermentStatementsForEori),
      "linkId",
      isNiAccount = false,
      serviceUnavailableUrl)

    val modelWithNoCurrentStatements: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
      accountNumber,
      Seq(dutyDefermentStatementsForEori.copy(currentStatements = Seq())),
      "linkId",
      isNiAccount = false,
      serviceUnavailableUrl)

    val modelWithNoCurrentAndRequestedStatements: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
      accountNumber,
      Seq(dutyDefermentStatementsForEori.copy(currentStatements = Seq(), requestedStatements = Seq())),
      "linkId",
      isNiAccount = false,
      serviceUnavailableUrl)

    def view(model: DutyDefermentAccountViewModel): Document =
      Jsoup.parse(app.injector.instanceOf[duty_deferment_account].apply(model).body)
  }
}
