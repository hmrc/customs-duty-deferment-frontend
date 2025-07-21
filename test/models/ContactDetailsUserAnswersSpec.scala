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

class ContactDetailsUserAnswersSpec extends SpecBase {

  "formats" should {
    "generate correct output for Json Reads" in new Setup {
      import ContactDetailsUserAnswers.formats

      Json.fromJson(Json.parse(userAnswersJsString)) mustBe JsSuccess(userAnswersOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(userAnswersOb) mustBe Json.parse(userAnswersJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"status\": \"pending\", \"eventId1\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[ContactDetailsUserAnswers]
      }
    }
  }

  "toEditContactDetails" should {
    "return correct EditContactDetailsUserAnswers" in new Setup {
      ContactDetailsUserAnswers
        .toEditContactDetails(
          validDan,
          dutyDefermentAccountLink,
          validAccountContactDetails
        ) mustBe EditContactDetailsUserAnswers(
        validDan,
        Some("Mr First Name"),
        Some("+44444111111"),
        Some("+55555222222"),
        Some("example@email.com"),
        isNiAccount = false
      )
    }
  }

  "toAddressDetails" should {
    "return correct EditAddressDetailsUserAnswers" in new Setup {
      private def countryName(inputValue: String = "UNITED KINGDOM") = Some(inputValue)

      ContactDetailsUserAnswers.toAddressDetails(
        validDan,
        dutyDefermentAccountLink,
        validAccountContactDetails,
        countryName
      ) mustBe EditAddressDetailsUserAnswers(
        "someDan",
        "Example Road",
        Some("Townsville"),
        Some("West County"),
        Some("London"),
        Some("AA00 0AA"),
        "GB",
        Some("GB"),
        false
      )
    }
  }

  trait Setup {
    val userAnswersOb: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
      validDan,
      Some("Example Name"),
      "Example Road",
      Some("street 1"),
      Some("sandyford close"),
      Some("88"),
      Some("RG17BB"),
      "GB",
      Some("United Kingdom"),
      Some("11111 222333"),
      None,
      Some("example@email.com"),
      isNiAccount = false
    )

    val userAnswersJsString: String =
      """{"dan":"someDan",
        |"name":"Example Name",
        |"addressLine1":"Example Road",
        |"addressLine2":"street 1",
        |"addressLine3":"sandyford close",
        |"addressLine4":"88",
        |"postCode":"RG17BB",
        |"countryCode":"GB",
        |"countryName":"United Kingdom",
        |"telephone":"11111 222333",
        |"email":"example@email.com",
        |"isNiAccount":false}""".stripMargin
  }
}
