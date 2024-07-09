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

package models.responses.retrieve

import play.api.libs.json.{JsSuccess, Json}
import util.SpecBase

class ContactDetailsSpec extends SpecBase {

  "Reads" should {

    "generate the correct object" in new Setup {

      import ContactDetails.format

      Json.fromJson(Json.parse(contactDetailsString)) mustBe JsSuccess(ob)
    }
  }

  "Writes" should {

    "generate correct contents" in new Setup {
      Json.toJson(ob) mustBe Json.parse(contactDetailsString)
    }
  }

  trait Setup {
    val ob: ContactDetails = ContactDetails(
      contactName = Some("John Smith"),
      addressLine1 = "1 High Street",
      addressLine2 = Some("Town"),
      addressLine3 = Some("The County"),
      addressLine4 = Some("England"),
      postCode = Some("AB12 3CD"),
      countryCode = "0044",
      telephone = Some("1234567"),
      faxNumber = Some("7654321"),
      email = Some("abc@de.com")
    )

    val contactDetailsString: String =
      """{
        |"addressLine1":"1 High Street",
        |"postCode":"AB12 3CD",
        |"telephone":"1234567",
        |"faxNumber":"7654321",
        |"email":"abc@de.com",
        |"addressLine4":"England",
        |"addressLine3":"The County",
        |"contactName":"John Smith",
        |"countryCode":"0044",
        |"addressLine2":"Town"}""".stripMargin
  }
}
