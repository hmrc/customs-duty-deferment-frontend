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

package views.contact_details

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.contact_details.edit_success_address
import util.SpecBase
import play.api.test.FakeRequest
import play.api.mvc.AnyContentAsEmpty

class EditSuccessAddressSpec extends SpecBase {
  "EditSuccessAddress view" should {
    "display recruitment info" in new Setup {
      view
        .getElementById("improve-the-service-subheader-text")
        .text()
        .contains(messages("user-research.subheader-text")) mustBe true

      view
        .getElementById("improve-the-service-body")
        .text()
        .contains(messages("user-research.help.body-text")) mustBe true

      view
        .getElementById("improve-the-service-link")
        .text()
        .contains(messages("user-research.help.link")) mustBe true

      view
        .getElementById("improve-the-service-link")
        .select("a")
        .attr("href") mustBe appConfig.helpMakeGovUkBetterUrl
    }
  }

  trait Setup {
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val dan  = "123"
    val isNi = false

    val view: Document = Jsoup.parse(
      instanceOf[edit_success_address]
        .apply(dan, isNi)(request, messages, appConfig)
        .body
    )
  }
}
