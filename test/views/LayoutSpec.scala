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

import config.AppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import util.SpecBase
import utils.Utils.emptyString
import views.html.Layout

class LayoutSpec extends SpecBase {

  "layout" should {

    "display correct guidance" when {

      "title and back link are provided" in new Setup {
        val title   = "test_title"
        val linkUrl = "test.com"

        val layoutView: Document = Jsoup.parse(
          application().injector
            .instanceOf[Layout]
            .apply(
              pageTitle = Some(title),
              backLink = Some(linkUrl)
            )(content)
            .body
        )

        shouldContainCorrectTitle(layoutView, title)(messages)
        shouldContainCorrectServiceUrls(layoutView)(appConfig)
        shouldContainCorrectBackLink(layoutView, Some(linkUrl))
        shouldContainCorrectBanners(layoutView)
      }

      "there is no value for title and back link" in new Setup {
        val layoutView: Document = Jsoup.parse(instanceOf[Layout].apply()(content).body)

        shouldContainCorrectTitle(layoutView)(messages)
        shouldContainCorrectServiceUrls(layoutView)(appConfig)
        shouldContainCorrectBackLink(layoutView)
        shouldContainCorrectBanners(layoutView)
      }
    }

  }

  private def shouldContainCorrectTitle(viewDoc: Document, title: String = emptyString)(implicit messages: Messages) =
    if (title.nonEmpty) {
      viewDoc.title() mustBe s"$title - ${messages("service.name")} - GOV.UK"
    } else {
      viewDoc.title() mustBe s"${messages("service.name")} - GOV.UK"
    }

  private def shouldContainCorrectServiceUrls(viewDoc: Document)(implicit appConfig: AppConfig) = {
    viewDoc.html().contains(appConfig.financialsHomepage) mustBe true
    viewDoc.html().contains(controllers.routes.LogoutController.logout.url) mustBe true
    viewDoc.html().contains("/accessibility-statement/customs-financials") mustBe true
  }

  private def shouldContainCorrectBackLink(viewDoc: Document, backLinkUrl: Option[String] = None) =
    if (backLinkUrl.isDefined) {
      viewDoc.getElementsByClass("govuk-back-link").text() mustBe "Back"
      viewDoc
        .getElementsByClass("govuk-back-link")
        .attr("href")
        .contains(backLinkUrl.get) mustBe true
    } else {
      viewDoc.getElementsByClass("govuk-back-link").text() mustBe "Back"
      viewDoc
        .getElementsByClass("govuk-back-link")
        .attr("href")
        .contains("#") mustBe true
    }

  private def shouldContainCorrectBanners(viewDoc: Document) = {
    viewDoc
      .getElementsByClass("govuk-phase-banner")
      .text() mustBe "BETA This is a new service – your feedback will help us to improve it."

    viewDoc
      .getElementsByClass("hmrc-user-research-banner")
      .text() mustBe "Help make GOV.UK better Sign up to take part in research (opens in new tab)" +
      " Hide message Hide message. I do not want to take part in research"
  }

  trait Setup {
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", "test_path")
    val content: Html                                         = Html("test")
  }
}
