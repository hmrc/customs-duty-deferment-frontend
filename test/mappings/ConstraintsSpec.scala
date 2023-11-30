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

package mappings

import play.api.data.validation.{Invalid, Valid, ValidationError}
import util.SpecBase

class ConstraintsSpec extends SpecBase with Constraints {

  "stripWhiteSpace method" should {
    "remove white space from beginning" in new SetUp {
      stripWhiteSpaces(emailWithLeadingSpaces.value) mustBe emailWithLeadingSpaces.value.trim
    }

    "remove white space from end" in new SetUp {
      stripWhiteSpaces(emailWithTrailingSpaces.value) mustBe emailWithLeadingSpaces.value.trim
    }

    "remove white space from front and back" in new SetUp {
      stripWhiteSpaces(emailWithLeadingAndTrailingSpaces.value) mustBe emailWithLeadingSpaces.value.trim
    }

    "remove white space from single middle" in new SetUp {
      stripWhiteSpaces(emailWithSpacesWithIn_2.value) mustBe email.value
    }

    "remove white space from multiple middle" in new SetUp {
      stripWhiteSpaces(emailWithSpacesWithIn_5.value) mustBe email.value
    }
  }

  "regex returns result" should {
    "email is valid" in new SetUp {
      isValid(email.value) mustBe true
    }

    "email is valid when stripped of whitespace" in new SetUp {
      isValid(stripWhiteSpaces(emailWithLeadingAndTrailingSpaces.value)) mustBe true
    }

    "email is invalid without .com or similiar domain" in new SetUp {
      isValid(invalidEmail_1.value) mustBe false
    }
  }

  "validPostcode return correct result" should {

    "postcode is valid" in new SetUp {
      validPostcode(postcode) mustBe Valid
    }
  }

  "return invalid" when {

    "postcode is invalid when postcode has empty spaces" in new SetUp {
      validPostcode(invalidPostcode_1) mustBe Invalid(ValidationError("accountDetails.edit.postcode.empty"))
    }

    "postcode is invalid when postcode has exceeded 8 characters" in new SetUp {
      validPostcode(invalidPostcode_2) mustBe Invalid(ValidationError("accountDetails.edit.postcode.max"))
    }

    "postcode is invalid when postcode has invalid regex" in new SetUp {
      validPostcode(invalidPostcode_3) mustBe Invalid(ValidationError("accountDetails.edit.postcode.invalid"))
    }
  }

  "validEmail return correct result" should {

    "email is valid" in new SetUp {
      isValidEmail(email) mustBe Valid
    }

    "email is valid when emailWithLeadingSpaces" in new SetUp {
      isValidEmail(emailWithLeadingSpaces) mustBe Valid
    }

    "email is valid when emailWithTrailingSpaces" in new SetUp {
      isValidEmail(emailWithTrailingSpaces) mustBe Valid
    }

    "email is valid when emailWithLeadingAndTrailingSpaces" in new SetUp {
      isValidEmail(emailWithLeadingAndTrailingSpaces) mustBe Valid
    }

    "email is valid when emailWithSpaces before @" in new SetUp {
      isValidEmail(emailWithSpacesWithIn_1) mustBe Valid
    }

    "email is valid when emailWithSpacees after @" in new SetUp {
      isValidEmail(emailWithSpacesWithIn_2) mustBe Valid
    }

    "email is valid when emailWithSpaces in domain" in new SetUp {
      isValidEmail(emailWithSpacesWithIn_3) mustBe Valid
    }

    "email is valid when emailWithSpaces spiltting name" in new SetUp {
      isValidEmail(emailWithSpacesWithIn_4) mustBe Valid
    }
  }

  "return invalid" when {
    "email is invalid email does not contain a .XYZ" in new SetUp {
      isValidEmail(invalidEmail_1) mustBe Invalid(List(ValidationError(List("emailAddress.edit.wrong-format"))))
    }

    "email is invalid when email has no @ or .XYZ" in new SetUp {
      isValidEmail(invalidEmail_2) mustBe Invalid(List(ValidationError(List("emailAddress.edit.wrong-format"))))
    }

    "email is invalid when email has not @" in new SetUp {
      isValidEmail(invalidEmail_3) mustBe Invalid(List(ValidationError(List("emailAddress.edit.wrong-format"))))
    }

    "email is invalid when email has no @ or front" in new SetUp {
      isValidEmail(invalidEmail_4) mustBe Invalid(List(ValidationError(List("emailAddress.edit.wrong-format"))))
    }

    "email is invalid when email is to long" in new SetUp {
      isValidEmail(invalidEmail_5) mustBe Invalid(List(ValidationError(List("emailAddress.edit.too-long"))))
    }

    "email is invalid when email is empty" in new SetUp {
      isValidEmail(invalidEmail_6) mustBe Invalid(List(ValidationError(List("emailAddress.edit.empty"))))
    }

    "email is invalid email is just white space" in new SetUp {
      isValidEmail(invalidEmail_7) mustBe Invalid(List(ValidationError(List("emailAddress.edit.empty"))))
    }
  }
}

trait SetUp {
  val spaces = Some("   ")
  val email = Some("abc@test.com")
  val emailWithLeadingSpaces = Some("   abc@test.com")
  val emailWithTrailingSpaces = Some("abc@test.com   ")
  val emailWithLeadingAndTrailingSpaces = Some("   abc@test.com   ")

  val emailWithSpacesWithIn_1 = Some("abc @test.com")
  val emailWithSpacesWithIn_2 = Some("abc@ test.com")
  val emailWithSpacesWithIn_3 = Some("abc@te  st.com")
  val emailWithSpacesWithIn_4 = Some("ab c@test.com")
  val emailWithSpacesWithIn_5 = Some("ab c@tes t.com")

  val invalidEmail_1 = Some("first@last")
  val invalidEmail_2 = Some("firstlast")
  val invalidEmail_3 = Some("first.com")
  val invalidEmail_4 = Some(".com")
  val invalidEmail_5 = Some("thisemailaddressisgreaterthan@132charactershenceinvalidthisemailaddressisgreaterthan@132charactershenceinvalid" +
    "thisemailaddressisgreaterthan@132charactershenceinvalidthisemailaddressisgreaterthan@132charactershenceinvalid.com")
  val invalidEmail_6 = Some("")
  val invalidEmail_7 = Some(" ")

  val postcode = "LS1 4AW"
  val invalidPostcode_1 = ""
  val invalidPostcode_2 = "LS11  4AE"
  val invalidPostcode_3 = "12345"

  val phoneNumber = "01133111122"
  val invalidPhone_1 = "A123456"
  val invalidPhone_2 = ""
  val invalidPhone_3 = "01112222333344446666777722334455"
}
