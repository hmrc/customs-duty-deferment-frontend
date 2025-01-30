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

package utils

import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.hmrcfrontend.views.html.components.HmrcNewTabLink
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.newtablink.NewTabLink
import util.SpecBase
import utils.ViewUtils._
import views.html.components.{caption, h1, h2, inset, link, p}

class ViewUtilsSpec extends SpecBase {

  "h1Component" should {
    "create the component correctly with provided input" in {
      h1Component(testMsgKey, Some(someId)) mustBe new h1().apply(msg = messages(testMsgKey), id = Some(someId))

      h1Component(testMsgKey, Some(someId), testClass) mustBe new h1()
        .apply(msg = messages(testMsgKey), id = Some(someId), classes = testClass)
    }
  }

  "emptyH2Component" should {
    "return the empty h2 component" in {
      emptyH2Component mustBe new h2()
    }
  }

  "h2Component" should {
    "create the component correctly with provided input" in {
      h2Component(msg = testMsg, id = Some(someId)) mustBe
        new h2().apply(msg = testMsg, id = Some(someId))

      h2Component(msg = testMsg, id = Some(someId), h2Class = Some(testClass)) mustBe
        new h2().apply(msg = testMsg, id = Some(someId), h2Class = Some(testClass))
    }
  }

  "emptyLinkComponent" should {
    "return the empty link component" in {
      emptyLinkComponent mustBe new link()
    }
  }

  "linkComponent" should {
    "create the component correctly with provided input" in {
      val result: HtmlFormat.Appendable = linkComponent(
        LinkComponentValues(
          pId = Some(someId),
          linkMessageKey = testMsgKey,
          location = testLocation,
          linkClass = testClass,
          preLinkMessageKey = Some(testMsgKey)
        )
      )

      result mustBe new link().apply(
        linkMessage = testMsgKey,
        location = testLocation,
        linkClass = testClass,
        preLinkMessage = Some(testMsgKey),
        pId = Some(someId)
      )
    }
  }

  "emptyPComponent" should {
    "return the empty p component" in {
      emptyPComponent mustBe new p()
    }
  }

  "pComponent" should {
    "create the component correctly with provided input" in {
      val result: HtmlFormat.Appendable = pComponent(
        content = Html(testMsg),
        id = Some(someId),
        classes = Some(testClass),
        tabLink = Some(
          new HmrcNewTabLink().apply(
            NewTabLink(
              language = Some(messages.lang.toString),
              classList = Some(testClass),
              href = Some(testHref),
              text = testMsg
            )
          )
        )
      )

      result mustBe new p().apply(
        id = Some(someId),
        classes = Some(testClass),
        content = Html(testMsg),
        tabLink = Some(
          new HmrcNewTabLink().apply(
            NewTabLink(
              language = Some(messages.lang.toString),
              classList = Some(testClass),
              href = Some(testHref),
              text = testMsg
            )
          )
        )
      )

      pComponent(content = Html(testMsg)) mustBe new p().apply(content = Html(testMsg))
    }
  }

  "insetComponent" should {
    "create the component correctly with provided input" in {
      insetComponent(msg = testMsg, id = Some(someId)) mustBe new inset().apply(msg = testMsg, id = Some(someDan))

      insetComponent(msg = testMsg, id = Some(someId), classes = Some(testClass)) mustBe
        new inset().apply(msg = testMsg, id = Some(someDan), classes = Some(testClass))
    }
  }

  "captionComponent" should {
    "create the component correctly with provided input" in {
      captionComponent(msg = testMsg, id = Some(someId), classes = testClass) mustBe new caption()
        .apply(msg = testMsg, id = Some(someDan), classes = testClass)

      captionComponent(msg = testMsg, classes = testClass) mustBe new caption()
        .apply(msg = testMsg, classes = testClass)
    }
  }

  "hmrcNewTabLinkComponent" should {
    "create the component correctly with provided input" in {
      val result: HtmlFormat.Appendable =
        hmrcNewTabLinkComponent(text = testMsg, href = Some(testHref), language = Some(testLang))

      result mustBe
        new HmrcNewTabLink().apply(NewTabLink(language = Some(testLang), href = Some(testHref), text = testMsg))
    }
  }
}
