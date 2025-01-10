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

import models.DefermentAccountAvailable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.SpecBase
import viewmodels.ContactDetailsViewModel
import views.html.contact_details.show

class ShowSpec extends SpecBase {

  "Duty Deferment Account Show Spec" should {
    "display header" in new Setup {
      running(application()) {
        view.getElementsByTag("h1").text mustBe "Duty deferment contact details"
      }
    }

    "display header2" in new Setup {
      running(application()) {
        view.getElementsByTag("h2").text mustBe
          "Help make GOV.UK better Account: someDan Support links"
      }
    }

    "when you click on the back link redirect to you contact details" in new Setup {
      running(application()) {
        val request = fakeRequest(GET, "http://localhost:9876/customs/payment-records/your-contact-details")
        val result  = route(application(), request).value
        val html    = Jsoup.parse(contentAsString(result))
        html.containsLinkWithText("/customs/payment-records/your-contact-details", "link-back")
      }
    }
  }

  trait Setup extends I18nSupport {

    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val someLinkId = "someLinkId"

    val validContactDetailsViewModel: ContactDetailsViewModel = ContactDetailsViewModel(
      validDan,
      validAccountContactDetails,
      _ => Some("United Kingdom")
    )

    def view: Document =
      Jsoup.parse(application().injector.instanceOf[show].apply(
        validContactDetailsViewModel, DefermentAccountAvailable, someLinkId)(request, messages, appConfig).body)

    override def messagesApi: MessagesApi = application().injector.instanceOf[MessagesApi]
  }
}
