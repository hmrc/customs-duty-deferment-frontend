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

package view

import config.AppConfig
import models.DefermentAccountAvailable
import org.jsoup.Jsoup
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.SpecBase
import viewmodels.ContactDetailsViewModel
import views.html.contact_details.show

class ShowSpec extends SpecBase {

  "Duty Deferment Account Show Spec" should {
    "display header" in new Setup {
      running(app) {
        view.getElementsByTag("h1").text mustBe "Account: someDan Contact details for duty deferment"
      }
    }

    "when you click on the back link redirect to you contact details" in new Setup {
      running(app) {
        val request = fakeRequest(GET, "http://localhost:9876/customs/payment-records/your-contact-details" )
        val result = route(app, request).value
        val html = Jsoup.parse(contentAsString(result))
        html.containsLinkWithText("/customs/payment-records/your-contact-details", "link-back")
      }
    }
  }

  trait Setup extends I18nSupport {

    implicit val request = FakeRequest("GET", "/some/resource/path")
    val app = application().build()
    implicit val appConfig = app.injector.instanceOf[AppConfig]

    val someLinkId = "someLinkId"

    val validContactDetailsViewModel: ContactDetailsViewModel = ContactDetailsViewModel(
      validDan,
      validAccountContactDetails,
      _ => Some("United Kingdom")
    )

    def view = Jsoup.parse(app.injector.instanceOf[show].apply(
      validContactDetailsViewModel, DefermentAccountAvailable,someLinkId).body)

    override def messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  }
}
