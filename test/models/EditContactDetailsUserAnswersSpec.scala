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
import models.responses.retrieve.ContactDetails

class EditContactDetailsUserAnswersSpec extends SpecBase {

  "EditContactDetailsUserAnswers" should {
    "convert to ContactDetailsUserAnswers correctly" in new Setup {

      val result: ContactDetailsUserAnswers = EditContactDetailsUserAnswers(
        someDan,
        nameOpt,
        telephoneOpt,
        faxOpt,
        emailOpt,
        isNiAccount
      )
        .toContactDetailsUserAnswers(
          initialContactDetails,
          isNiAccount,
          getCountryNameF
        )

      result.dan mustBe someDan
      result.name mustBe nameOpt
      result.telephone mustBe telephoneOpt
      result.fax mustBe faxOpt
      result.email mustBe emailOpt
      result.isNiAccount mustBe isNiAccount
      result.addressLine1 mustBe initialContactDetails.addressLine1
      result.addressLine2 mustBe initialContactDetails.addressLine2
      result.addressLine3 mustBe initialContactDetails.addressLine3
      result.addressLine4 mustBe initialContactDetails.addressLine4
      result.postCode mustBe initialContactDetails.postCode
      result.countryCode mustBe initialContactDetails.countryCode
      result.countryName mustBe Some("United Kingdom")
    }
  }

  trait Setup {
    val isNiAccount = true

    val initialContactDetails: ContactDetails = ContactDetails(
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

    val getCountryNameF: String => Option[String] = _ => Some("United Kingdom")
  }
}
