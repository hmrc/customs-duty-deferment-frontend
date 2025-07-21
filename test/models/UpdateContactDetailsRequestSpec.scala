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

import play.api.libs.json.{JsResultException, JsSuccess, Json}
import util.SpecBase

class UpdateContactDetailsRequestSpec extends SpecBase {

  "UpdateContactDetailsRequest" should {

    "apply method returns UpdateContactDetailsRequest for non-null postcode" in {

      val validContactDetailsUserAnswers: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
        validDan,
        Some("Example Name"),
        "Example Road",
        None,
        None,
        None,
        Some("SW1A 2BQ"),
        "GB",
        Some("United Kingdom"),
        Some("11111 222333"),
        None,
        Some("example@email.com"),
        isNiAccount = false
      )

      val request: UpdateContactDetailsRequest =
        UpdateContactDetailsRequest.apply("someDan", "someEori", validContactDetailsUserAnswers)

      assert(request.postCode.contains("SW1A 2BQ"))
    }

    "with WhitespaceTrimmed method  should trim whitespaces of data in ContactDetailsUserAnswers" in {

      val validContactDetailsUserAnswers: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
        validDan,
        Some("Example Name"),
        "Example Road 01 ",
        Option("Example Road 02    "),
        Option("Example Road 03 "),
        Option("Example Road 04 "),
        Some("SW1A 2BQ "),
        "GB",
        Some("United Kingdom"),
        Some("11111 222333"),
        Some("11111 222334 "),
        Some("example@email.com"),
        isNiAccount = false
      )
      val request: UpdateContactDetailsRequest                      =
        UpdateContactDetailsRequest.apply("someDan", "someEori", validContactDetailsUserAnswers.withWhitespaceTrimmed)
      assert(request.postCode.contains("SW1A 2BQ"))
      assert(request.addressLine2.contains("Example Road 02"))
    }

    "apply method returns UpdateContactDetailsRequest for null postcode" in {

      val invalidContactDetailsUserAnswers: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
        validDan,
        Some("Example Name"),
        "Example Road",
        None,
        None,
        None,
        Some(""),
        "GB",
        Some("United Kingdom"),
        Some("11111 222333"),
        None,
        Some("example@email.com"),
        isNiAccount = false
      )
      val request: UpdateContactDetailsRequest                        =
        UpdateContactDetailsRequest.apply("someDan", "someEori", invalidContactDetailsUserAnswers)
      assert(request.postCode.isEmpty)
    }

    "formats" should {
      "generate correct output for Json Reads" in new Setup {

        import UpdateContactDetailsRequest.formats

        Json.fromJson(Json.parse(updateContactDetailsReqJsString)) mustBe JsSuccess(updateContactDetailsReqOb)
      }

      "generate correct output for Json Writes" in new Setup {
        Json.toJson(updateContactDetailsReqOb) mustBe Json.parse(updateContactDetailsReqJsString)
      }

      "throw exception for invalid Json" in {
        val invalidJson = "{ \"status\": \"pending\", \"eventId1\": \"test_event\" }"

        intercept[JsResultException] {
          Json.parse(invalidJson).as[UpdateContactDetailsRequest]
        }
      }
    }
  }

  trait Setup {
    val validContactDetailsUserAnswers: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
      validDan,
      Some("Example Name"),
      "Example Road",
      None,
      None,
      None,
      Some("SW1A 2BQ"),
      "GB",
      Some("United Kingdom"),
      Some("11111 222333"),
      None,
      Some("example@email.com"),
      isNiAccount = false
    )

    val updateContactDetailsReqOb: UpdateContactDetailsRequest =
      UpdateContactDetailsRequest(validDan, "someEori", validContactDetailsUserAnswers)

    val updateContactDetailsReqJsString: String =
      """{"dan":"someDan",
        |"eori":"someEori",
        |"name":"Example Name",
        |"addressLine1":"Example Road",
        |"postCode":"SW1A 2BQ",
        |"countryCode":"GB",
        |"telephone":"11111 222333",
        |"email":"example@email.com"
        |}""".stripMargin
  }
}
