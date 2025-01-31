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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import views.html.service_unavailable

class ServiceUnavailableSpec extends SpecBase {

  "ServiceUnavailable view" should {
    "display correct title and guidance" in new Setup {
      view.title() mustBe
        s"${messages("cf.service-unavailable.title")} - ${messages("service.name")} - GOV.UK"

      view
        .getElementById("service-unavailable.heading")
        .text()
        .contains(messages("cf.service-unavailable.heading")) mustBe true

      view
        .getElementById("older-statement-guidance-text")
        .text() must not be empty

      view
        .getElementById("older-statement-guidance-text")
        .text()
        .contains(
          s"${messages("cf.service-unavailable.description.1")} " +
            s"${messages("cf.service-unavailable.description.2")}"
        ) mustBe true

      view.html().contains(testLinkUrl)
      view.html().contains(messages("cf.service-unavailable.description.3"))
      view.html().contains(deskProLink)
    }
  }

  trait Setup {
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")
    val deskProLink: String                                   = "http://localhost:9250" +
      "/contact/report-technical-problem?newTab=true&amp;service=CDS%20FinancialsreferrerUrl=test_Path"

    val view: Document =
      Jsoup.parse(
        instanceOf[service_unavailable]
          .apply(Option(testLinkUrl))(request, messages, appConfig)
          .body
      )
  }
}
