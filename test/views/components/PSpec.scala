/*
 * Copyright 2025 HM Revenue & Customs
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

package views.components

import play.twirl.api.Html
import util.SpecBase

class PSpec extends SpecBase {

  "ParagraphComponent" should {

    "render only content when no links present" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = None,
          classes = None,
          link = None,
          tabLink = None
        )
        .body
        .trim

      rendered mustBe s"""<p class="$DefaultClass">$ContentOnly</p>"""
    }

    "render content and link with period when link present" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = None,
          classes = None,
          link = Some(Html(LinkHtml)),
          tabLink = None
        )
        .body
        .trim

      rendered mustBe s"""<p class="$DefaultClass">$ContentOnly $LinkHtml$Period</p>"""
    }

    "render content and tabLink with period when tabLink present" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = None,
          classes = None,
          link = None,
          tabLink = Some(Html(TabLinkHtml))
        )
        .body
        .trim

      rendered mustBe s"""<p class="$DefaultClass">$ContentOnly $TabLinkHtml$Period</p>"""
    }

    "render content with both link and tabLink and a period" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = None,
          classes = None,
          link = Some(Html(LinkHtml)),
          tabLink = Some(Html(TabLinkHtml))
        )
        .body
        .trim

      rendered mustBe s"""<p class="$DefaultClass">$ContentOnly $LinkHtml $TabLinkHtml$Period</p>"""
    }

    "render content with id attribute when id present" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = Some(IdValue),
          classes = None,
          link = None,
          tabLink = None
        )
        .body
        .trim

      rendered mustBe s"""<p class="$DefaultClass" id="$IdValue">$ContentOnly</p>"""
    }

    "render content with custom classes when classes present" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = None,
          classes = Some(ClassesValue),
          link = None,
          tabLink = None
        )
        .body
        .trim

      rendered mustBe s"""<p class="$ClassesValue">$ContentOnly</p>"""
    }

    "render content with both id and custom classes" in new Setup {
      val rendered = new views.html.components.p()
        .apply(
          content = Html(ContentOnly),
          id = Some(IdValue),
          classes = Some(ClassesValue),
          link = None,
          tabLink = None
        )
        .body
        .trim

      rendered mustBe s"""<p class="$ClassesValue" id="$IdValue">$ContentOnly</p>"""
    }
  }

  trait Setup {
    val ContentOnly  = "Sample content"
    val LinkHtml     = "<a href=\"link\">Link</a>"
    val TabLinkHtml  = "<a href=\"tab\" target=\"_blank\">Tab</a>"
    val Period       = "."
    val DefaultClass = "govuk-body"
    val IdValue      = "my-id"
    val ClassesValue = "custom-class another-class"
  }
}
