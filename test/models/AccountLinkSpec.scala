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

package models

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class AccountLinkSpec extends SpecBase {

  "format" should {
    "generate correct output for Json Reads" in new Setup {
      import AccountLink.format

      Json.fromJson(Json.parse(accountLinkJsString)) mustBe JsSuccess(accountLinkOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(accountLinkOb) mustBe Json.parse(accountLinkJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"status\": \"pending\", \"eventId1\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[AccountLink]
      }
    }
  }

  trait Setup {
    val accountLinkOb: AccountLink  = AccountLink(
      eori = validEori,
      accountNumber = validDan,
      linkId = testId,
      accountStatus = AccountStatusOpen,
      accountStatusId = None,
      isNiAccount = false
    )
    val accountLinkJsString: String =
      """{"eori":"someEori",
        |"accountNumber":"someDan",
        |"linkId":"test_123",
        |"accountStatus":"open",
        |"isNiAccount":false
        |}""".stripMargin
  }
}
