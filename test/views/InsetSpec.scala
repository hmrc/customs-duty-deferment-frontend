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
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import util.SpecBase
import views.html.components.inset

class InsetSpec extends SpecBase {
  "Inset Component" should {
    "render component correctly" in new SetUp {
      running(application()) {
        val output: HtmlFormat.Appendable = insetView(
          id = Some("div-id"),
          msg = "Hello world!",
          classes = None
        )(messages)
        val html                          = parseHtml(output)

        html.getElementById("div-id").text must include("Hello world!")
        html.getElementById("div-id").hasClass("govuk-!-margin-top-7") mustBe true
        html.getElementById("div-id").hasClass("govuk-!-margin-bottom-9") mustBe true
      }
    }

    "render component correctly without an ID" in new SetUp {
      running(application()) {
        val output: HtmlFormat.Appendable = insetView(
          id = None,
          msg = "Hello world!",
          classes = None
        )(messages)
        val html                          = parseHtml(output)

        html.getElementsByTag("div").text must include("Hello world!")
        html.getElementsByTag("div").hasClass("govuk-!-margin-top-7") mustBe true
        html.getElementsByTag("div").hasClass("govuk-!-margin-bottom-9") mustBe true
      }
    }

    "render component correctly with custom classes" in new SetUp {
      running(application()) {
        val output: HtmlFormat.Appendable = insetView(
          id = Some("div-id"),
          msg = "Hello world!",
          classes = Some("custom-class")
        )(messages)
        val html                          = parseHtml(output)

        html.getElementById("div-id").text must include("Hello world!")
        html.getElementById("div-id").hasClass("custom-class") mustBe true
      }
    }
  }

  trait SetUp {
    val insetView        = application().injector.instanceOf[inset]

    def parseHtml(output: HtmlFormat.Appendable): Document =
      Jsoup.parse(contentAsString(output))
  }
}
