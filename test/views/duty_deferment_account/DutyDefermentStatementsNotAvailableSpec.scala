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
import views.html.duty_deferment_account.duty_deferment_statements_not_available

class DutyDefermentStatementsNotAvailableSpec extends SpecBase {
  "DutyDefermentStatementsNotAvailable view" should {
    "display correct title and guidance" in new Setup {
      view.title() mustBe
        s"${messages(app)("service.name")} - ${messages(app)("service.name")} - GOV.UK"

      view.getElementById("statements-heading").text() mustBe
        messages(app)("cf.account.detail.deferment-account-heading")

      view.getElementById("no-statements").getElementsByTag("p").html() mustBe
        messages(app)("cf.duty-deferment-account.problem-with-service.text")

      view.getElementById("missing-documents-guidance-heading").text() mustBe
        messages(app)("cf.common.missing-documents-guidance.cdsStatements.heading")

      view.getElementById("chief-guidance-heading").text() mustBe
        messages(app)("cf.common.chiefStatements.heading")

      view.getElementById("dd-support-message-heading").text() mustBe
        messages(app)("cf.accounts.support.heading")

      view.html().contains(messages(app)("cf.accounts.older-statements.description.link"))
      view.html().contains(messages(app)("cf.accounts.older-statements.description"))
      view.html().contains(serviceUnavailableUrl.get)
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

    val view: Document = Jsoup.parse(
      app.injector.instanceOf[duty_deferment_statements_not_available].apply(
        accountNumber, linkId, serviceUnavailableUrl).body)
  }
}
