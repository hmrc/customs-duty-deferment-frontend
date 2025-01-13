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

package views.components

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import util.SpecBase
import views.html.components.p2

class P2Spec extends SpecBase {

  "component" should {
    "display the correct contents" when {
      "component has an id" in new Setup {

        p2Component.getElementById(id).text().contains(messages(messageKey)) mustBe true

        p2Component.getElementsByClass("govuk-body").text().contains(messages(messageKey)) mustBe true

        val pElement: Elements = p2Component.getElementsByTag("p")
        pElement.size() mustBe 1
      }

      "component has no id" in new Setup {
        p2ComponentWithNoId.getElementsByClass("govuk-body").text().contains(messages(messageKey)) mustBe true

        val pElement: Elements = p2ComponentWithNoId.getElementsByTag("p")
        pElement.size() mustBe 1
      }
    }

    trait Setup {
      val id         = "undelivered-pi"
      val messageKey = "cf.undeliverable.email.p1"

      val p2Component: Document = Jsoup.parse(
        application().injector.instanceOf[p2].apply(message = messageKey, id = Some(id)).body
      )

      val p2ComponentWithNoId: Document = Jsoup.parse(
        application().injector.instanceOf[p2].apply(message = messageKey).body
      )
    }
  }
}
