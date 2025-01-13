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
import org.jsoup.select.Elements
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import views.html.not_subscribed_to_cds

class NotSubscribedToCdsSpec extends SpecBase {

  "NotSubscribedToCds view" should {
    "display correct title and guidance" in new Setup {
      view.title() mustBe
        s"${messages("cf.not-subscribed-to-cds.detail.title")} - ${messages("service.name")} - GOV.UK"

      view.getElementsByTag("h1").html().contains(
        messages("cf.not-subscribed-to-cds.detail.heading")) mustBe true

      val h2Elements: Elements = view.getElementsByTag("h2")

      h2Elements.get(1).html().contains(
        messages("cf.not-subscribed-to-cds.detail.already-subscribed-to-cds")) mustBe true

      h2Elements.get(2).html().contains(
        messages("cf.not-subscribed-to-cds.details.subscribe-to-cds")) mustBe true

      val pElements: Elements = view.getElementsByTag("p")

      pElements.get(1).html().contains(
        messages("cf.not-subscribed-to-cds.detail.already-subscribed-to-cds-guidance-text")) mustBe true

      view.html().contains(cdsSubscribeUrl)
      view.html().contains(messages("cf.not-subscribed-to-cds.details.subscribe-to-cds-link-text"))

      view.html().contains(deskProLinkText)
    }
  }

  trait Setup {
    val deskProLinkText = "Is this page not working properly? (opens in new tab)"
    val cdsSubscribeUrl = "https://www.tax.service.gov.uk/customs-enrolment-services/cds/subscribe"

    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val view: Document =
      Jsoup.parse(application().injector.instanceOf[not_subscribed_to_cds].apply()(request, messages, appConfig).body)
  }
}
