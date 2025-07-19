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

package viewmodels

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class ContactDetailsViewModelSpec extends SpecBase {

  "formatsDutyDefermentAccountDetails" should {
    "generate correct output for Json Reads" in new Setup {
      import ContactDetailsViewModel.formatsDutyDefermentAccountDetails

      Json.fromJson(Json.parse(contactDetailsViewModelJsString)) mustBe JsSuccess(contactDetailsViewModelOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(contactDetailsViewModelOb) mustBe Json.parse(contactDetailsViewModelJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"dan1\": \"someDan\", \"addressLine1\": \"london street\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[ContactDetailsViewModel]
      }
    }
  }

  trait Setup {
    val contactDetailsViewModelOb: ContactDetailsViewModel = ContactDetailsViewModel(
      validDan,
      validAccountContactDetails,
      _ => Some("United Kingdom")
    )

    val contactDetailsViewModelJsString: String =
      """{"dan":"someDan",
        |"name":"Mr First Name",
        |"addressLine1":"Example Road",
        |"addressLine2":"Townsville",
        |"addressLine3":"West County",
        |"addressLine4":"London",
        |"postCode":"AA00 0AA",
        |"countryCode":"GB",
        |"countryName":"United Kingdom",
        |"telephone":"+44444111111",
        |"fax":"+55555222222",
        |"email":"example@email.com"
        |}""".stripMargin
  }
}
