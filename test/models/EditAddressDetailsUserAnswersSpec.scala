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
import models.ContactDetailsUserAnswers
import util.SpecBase

class EditAddressDetailsUserAnswersSpec extends SpecBase {

    "EditAddressDetailsUserAnswers" should {
    "convert to ContactDetailsUserAnswers correctly" in new Setup{

      val convertedContactDetailsUserAnswers = editAddressDetailsUserAnswers
        .toContactDetailsUserAnswers(initialContactDetails, isNiAccount = true)

      convertedContactDetailsUserAnswers mustBe expectedContactDetailsUserAnswers
    }
  }

  trait Setup {
      val initialContactDetails = ContactDetails(
        contactName = Some("John Doe"),
        addressLine1 = "123 Main St",
        addressLine2 = Some("line 2"),
        addressLine3 = Some("line 3"),
        addressLine4 = Some("line 4"),
        postCode = Some("12345"),
        countryCode = "UK",
        telephone = Some("123-456-7890"),
        faxNumber = Some("987-654-3210"),
        email = Some("john.doe@example.com")
      )

      val editAddressDetailsUserAnswers = EditAddressDetailsUserAnswers(
        dan = "123456",
        addressLine1 = "123 Main St",
        addressLine2 = Some("Apt 4B"),
        addressLine3 = None,
        addressLine4 = None,
        postCode = Some("12345"),
        countryCode = "0044",
        countryName = Some("United Kingdom"),
        isNiAccount = true
      )

      val expectedContactDetailsUserAnswers = ContactDetailsUserAnswers(
        dan = "123456",
        name = Some("John Doe"),
        addressLine1 = "123 Main St",
        addressLine2 = Some("Apt 4B"),
        addressLine3 = None,
        addressLine4 = None,
        postCode = Some("12345"),
        countryCode = "0044",
        countryName = Some("United Kingdom"),
        telephone = Some("123-456-7890"),
        fax = Some("987-654-3210"),
        email = Some("john.doe@example.com"),
        isNiAccount = true
      )
  }
}
