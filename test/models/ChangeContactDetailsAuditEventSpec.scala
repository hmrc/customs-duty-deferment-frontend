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

import models.responses.retrieve.ContactDetails
import play.api.libs.json.{JsSuccess, Json}
import util.SpecBase

class ChangeContactDetailsAuditEventSpec extends SpecBase {

  "Reads" should {

    "generate correct object" in new Setup {

      import ChangeContactDetailsAuditEvent.formats

      Json.fromJson(Json.parse(changeContactDetailsJsString)) mustBe JsSuccess(changeContactDetailsOb)
    }
  }

  "Writes" should {

    "write the correct contents" in new Setup {
      Json.toJson(changeContactDetailsOb) mustBe Json.parse(changeContactDetailsJsString)
    }
  }

  trait Setup {
    val eori                                   = "test_eori"
    val previousContactDetails: ContactDetails = ContactDetails(
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

    val updatedContactDetails: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
      dan = "new dan",
      name = Some("John Smith"),
      addressLine1 = "2 Main Street",
      addressLine2 = Some("Town"),
      addressLine3 = Some("The County"),
      addressLine4 = Some("Highlands"),
      postCode = Some("SC12 3CD"),
      countryCode = "0045",
      countryName = Some("Scotland"),
      telephone = Some("1234567"),
      fax = Some("7654321"),
      email = Some("abc@de.com"),
      isNiAccount = false
    )

    val changeContactDetailsOb: ChangeContactDetailsAuditEvent =
      ChangeContactDetailsAuditEvent(eori, previousContactDetails, updatedContactDetails)

    val changeContactDetailsJsString: String =
      """{
        |"dutyDefermentAccountNumber":"new dan",
        |"eori":"test_eori",
        |"from":{"addressLine1":"1 High Street",
        |"postCode":"AB12 3CD",
        |"telephone":"1234567",
        |"faxNumber":"7654321",
        |"email":"abc@de.com",
        |"addressLine4":"England",
        |"addressLine3":"The County",
        |"contactName":"John Smith",
        |"countryCode":"0044","addressLine2":"Town"},
        |"to":{"addressLine1":"2 Main Street",
        |"postCode":"SC12 3CD",
        |"telephone":"1234567",
        |"faxNumber":"7654321",
        |"email":"abc@de.com",
        |"addressLine4":"Highlands",
        |"addressLine3":"The County",
        |"contactName":"John Smith",
        |"countryCode":"0045",
        |"addressLine2":"Town"}}""".stripMargin
  }
}
