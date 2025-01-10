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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.Assertion
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

      shouldDisplayCorrectCommonGuidanceAndText(viewDoc, messages, accountNumber, serviceUnavailableUrl)
      viewDoc.getElementById("request-statement-link").text() must not be empty
      showAllSectionText.r.findAllIn(viewDoc.html()).length mustBe 1
    }

    "display correct title and guidance when there is no current statements" in new Setup {
      val viewDoc: Document = view(modelWithNoCurrentStatements)
      val htmlDoc: String   = viewDoc.html()

      shouldDisplayCorrectCommonGuidanceAndText(viewDoc, messages, accountNumber, serviceUnavailableUrl)

      viewDoc.getElementById("request-statement-link").text() must not be empty
      htmlDoc.contains(messages("cf.account.detail.no-statements", accountNumber))
      showAllSectionText.r.findAllIn(htmlDoc).length mustBe 0
    }

    "display correct title and guidance when there is no requested and current statements" in new Setup {
      val viewDoc: Document = view(modelWithNoCurrentAndRequestedStatements)
      val htmlDoc: String   = viewDoc.html()

      shouldDisplayCorrectCommonGuidanceAndText(viewDoc, messages, accountNumber, serviceUnavailableUrl)

      Option(viewDoc.getElementById("request-statement-link")) mustBe empty
      htmlDoc.contains(messages("cf.account.detail.no-statements", accountNumber))
      showAllSectionText.r.findAllIn(htmlDoc).length mustBe 0
    }
  }

  private def shouldDisplayCorrectCommonGuidanceAndText(
    viewDoc: Document,
    messages: Messages,
    accountNumber: String,
    serviceUnavailableUrl: String
  ): Assertion = {
    viewDoc.title() mustBe
      s"${messages("cf.account.detail.title")} - ${messages("service.name")} - GOV.UK"

    viewDoc.getElementById("eori-heading").text() mustBe messages("cf.account-number", accountNumber)

    viewDoc.getElementById("statements-heading").text() mustBe
      messages("cf.account.detail.deferment-account-heading")

    viewDoc.getElementById("direct-debit-info").text() mustBe
      messages("cf.account.detail.direct-debit.duty-vat-and-excise")

    viewDoc.getElementById("missing-documents-guidance-heading").text() must not be empty
    viewDoc.getElementById("missing-documents-guidance-text1").text()   must not be empty
    viewDoc.getElementById("missing-documents-guidance-heading").text() mustBe
      messages("cf.common.missing-documents-guidance.cdsStatements.heading")

    viewDoc.getElementById("chief-guidance-heading").text()         must not be empty
    viewDoc.getElementById("chief-documents-guidance-text1").text() must not be empty
    viewDoc.getElementById("chief-guidance-heading").text() mustBe
      messages("cf.common.chiefStatements.heading")

    viewDoc.getElementById("dd-support-message-heading").text() mustBe messages("cf.accounts.support.heading")
    viewDoc.getElementById("dd-support-message-heading").text() must not be empty
    viewDoc.getElementById("dd-support-message").text()         must not be empty

    viewDoc.html().contains(serviceUnavailableUrl) mustBe true
  }

  trait Setup {
    
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val serviceUnavailableUrl: String = "service_unavailable_url"
    val accountNumber                 = "1234567"
    val linkId                        = "link_id"
    val showAllSectionText: String    = messages("cf.account.detail.accordion.show-all-sections")

    val model: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
      accountNumber,
      Seq(dutyDefermentStatementsForEori01),
      "linkId",
      isNiAccount = false,
      serviceUnavailableUrl
    )(appConfig, messages)

    val modelWithNoCurrentStatements: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
      accountNumber,
      Seq(dutyDefermentStatementsForEori01.copy(currentStatements = Seq())),
      "linkId",
      isNiAccount = false,
      serviceUnavailableUrl
    )(appConfig, messages)

    val modelWithNoCurrentAndRequestedStatements: DutyDefermentAccountViewModel = DutyDefermentAccountViewModel(
      accountNumber,
      Seq(dutyDefermentStatementsForEori01.copy(currentStatements = Seq(), requestedStatements = Seq())),
      "linkId",
      isNiAccount = false,
      serviceUnavailableUrl
    )(appConfig, messages)

    def view(model: DutyDefermentAccountViewModel): Document =
      Jsoup.parse(application().injector.instanceOf[duty_deferment_account].apply(model)(
        request, messages, appConfig).body)
  }
}
