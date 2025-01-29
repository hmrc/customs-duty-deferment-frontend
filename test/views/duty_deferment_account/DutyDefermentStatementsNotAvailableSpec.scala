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
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import views.html.duty_deferment_account.duty_deferment_statements_not_available
import org.scalatest.matchers.must.Matchers._

class DutyDefermentStatementsNotAvailableSpec extends SpecBase {
  "DutyDefermentStatementsNotAvailable view" should {
    "display correct title and guidance" in new Setup {
      view.title() mustBe
        s"${messages("service.name")} - ${messages("service.name")} - GOV.UK"

      view
        .getElementById("statements-heading")
        .text()
        .contains(messages("cf.account.detail.deferment-account-heading")) mustBe true

      view
        .getElementById("no-statements")
        .getElementsByTag("p")
        .html()
        .contains(messages("cf.duty-deferment-account.problem-with-service.text")) mustBe true

      view
        .getElementById("missing-documents-guidance-heading")
        .text()
        .contains(messages("cf.common.missing-documents-guidance.cdsStatements.heading")) mustBe true

      view
        .getElementById("chief-guidance-heading")
        .text()
        .contains(messages("cf.common.chiefStatements.heading")) mustBe true

      view
        .getElementById("dd-support-message-heading")
        .text()
        .contains(messages("cf.accounts.support.heading")) mustBe true

      view.html().contains(messages("cf.accounts.older-statements.description.link"))
      view.html().contains(messages("cf.accounts.older-statements.description"))
      view.html().contains(serviceUnavailableUrl.get)
    }
  }

  trait Setup {
    val serviceUnavailableUrl: Option[String] = Option("service_unavailable_url")

    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val view: Document = Jsoup.parse(
      application().injector
        .instanceOf[duty_deferment_statements_not_available]
        .apply(accNumber, someLinkId, serviceUnavailableUrl)(request, messages, appConfig)
        .body
    )
  }
}
