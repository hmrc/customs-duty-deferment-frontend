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
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import utils.Utils.emptyString
import viewmodels.DutyDefermentAccountViewModel
import views.html.duty_deferment_account.duty_deferment_account

class DutyDefermentAccountSpec extends SpecBase {

  "DutyDefermentAccount view" should {

    "display correct title and guidance" in new Setup {
      view.title() mustBe
        s"${messages(app)("cf.account.detail.title")} - ${messages(app)("service.name")} - GOV.UK"

      view.getElementById("eori-heading").text() mustBe messages(app)("cf.account-number", accountNumber)

      view.getElementById("statements-heading").text() mustBe
        messages(app)("cf.account.detail.deferment-account-heading")

      view.getElementById("direct-debit-info").text() mustBe
        messages(app)("cf.account.detail.direct-debit.duty-vat-and-excise")

      view.getElementById("missing-documents-guidance-heading").text() mustBe
        messages(app)("cf.common.missing-documents-guidance.cdsStatements.heading")

      view.getElementById("chief-guidance-heading").text() mustBe
        messages(app)("cf.common.chiefStatements.heading")

      view.getElementById("dd-support-message-heading").text() mustBe messages(app)("cf.accounts.support.heading")

      view.getElementById("missing-documents-guidance-text1").text() must not be empty

      view.getElementById("chief-documents-guidance-text1").text() must not be empty

      view.html().contains("cf.accounts.older-statements.description.link")
      view.html().contains("cf.accounts.older-statements.description")
      view.html().contains("cf.accounts.chiefStatements.description")
      view.html().contains(serviceUnavailableUrl.getOrElse(emptyString))
    }
  }

  trait Setup {
    val app: Application = application().build()
    val serviceUnavailableUrl: Option[String] = Option("service_unavailable_url")
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
      serviceUnavailableUrl.get)

    val view: Document = Jsoup.parse(app.injector.instanceOf[duty_deferment_account].apply(model).body)
  }
}
