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

import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import utils.Utils._

class UtilsSpec extends SpecBase {
  "emptyString" should {
    "contain the empty string" in {
      Utils.emptyString mustBe empty
    }
  }

  "semiColon" should {
    "return correct value" in {
      semiColon mustBe ":"
    }
  }

  "doubleForwardSlash" should {
    "return correct value" in {
      doubleForwardSlash mustBe "//"
    }
  }

  "pathWithQueryString" should {
    "return correct value" in {
      val path = "somePath"
      pathWithQueryString(fakeRequest("GET", path)) mustBe s"$path"
    }
  }

  "httpsProtocol" should {
    "return correct value" in {
      httpsProtocol mustBe "https"
    }
  }

  "localhostString" should {
    "return correct value" in {
      localhostString mustBe "localhost"
    }
  }

  "referrerUrl" should {
    "return correct value when platform host has some value" in {
      val path = "somePath"
      val platformHost = "localhost"
      implicit val reqHeaders: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", path)

      referrerUrl(Some(platformHost)) mustBe Option(s"$platformHost$path")
    }

    "return correct value when platform host value is empty" in {
      val path = "somePath"
      implicit val reqHeaders: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", path)

      referrerUrl(None) mustBe Option(s"$path")
    }
  }
}
