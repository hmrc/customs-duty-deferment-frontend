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
import util.SpecBase

class EditContactDetailsSpec extends SpecBase {

  "EditContactDetailsUserAnswers" should {
    "EditContactDetailsUserAnswers can be created" in new Setup {
      val someDetails = new EditContactDetailsUserAnswers(
        dan = "someDan",
        name = Option("someName"),
        telephone = Option("1234567"),
        fax = Option("fax"),
        email = Option("abc@def.com"),
        isNiAccount = false
      )

      someDetails mustBe userAnswers
    }
  }

  trait Setup {

    val userAnswers = new EditContactDetailsUserAnswers(
      "someDan",
      Some("someName"),
      Some("1234567"),
      Some("fax"),
      Some("abc@def.com"),
      false
    )

    val contactDetails = new ContactDetails(
      Some("someName"),
      "address1",
      Some("address2"),
      Some("address3"),
      Some("address4"),
      Some("somePostCode"),
      "GB",
      Some("1335678"),
      Some("fax"),
      Some("abc@def.com")
    )

    val contactDetailsUserAnswers = new ContactDetailsUserAnswers(
      "someName",
      Some("name"),
      "address1",
      Some("address2"),
      Some("address3"),
      Some("address4"),
      Some("somePostCode"),
      "GB",
      Option("someCountry"),
      Some("1335678"),
      Some("fax"),
      Some("abc@def.com"),
      false
    )

    val editContact = new EditContactDetailsUserAnswers(
      dan = "someDan",
      name = Option("someName"),
      telephone = Option("1234567"),
      fax = Option("fax"),
      email = Option("abc@def.com"),
      isNiAccount = false
    )
  }
}
