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

import models.EditAddressDetailsUserAnswers
import play.api.data.Form
import util.SpecBase
import utils.Utils.emptyString
import play.api.data.FormError

class EditAddressDetailsFormProviderSpec extends SpecBase {

  "Form" should {

    "bind the values successfully" when {

      "all required values are provided" in new SetUp {
        val validForm: Form[EditAddressDetailsUserAnswers] =
          new EditAddressDetailsFormProvider()
            .apply()
            .bind(
              Map(
                "dan"          -> someDan,
                "addressLine1" -> addressLine1,
                "postCode"     -> postCode,
                "countryCode"  -> countryCode
              )
            )

        val addressDetailsForm: EditAddressDetailsUserAnswers = validForm.get

        addressDetailsForm.dan mustBe someDan
        addressDetailsForm.addressLine1 mustBe addressLine1
        addressDetailsForm.postCode mustBe Some(postCode)
        addressDetailsForm.countryCode mustBe countryCode
      }

      "all required values and optional values are provided" in {
        val validForm: Form[EditAddressDetailsUserAnswers] =
          new EditAddressDetailsFormProvider()
            .apply()
            .bind(
              Map(
                "dan"          -> someDan,
                "addressLine1" -> addressLine1,
                "addressLine2" -> addressLine2,
                "addressLine3" -> addressLine3,
                "addressLine4" -> addressLine4,
                "postCode"     -> postCode,
                "countryCode"  -> countryCode,
                "countryName"  -> countryName,
                "isNiAccount"  -> "false"
              )
            )

        val addressDetailsForm: EditAddressDetailsUserAnswers = validForm.get

        addressDetailsForm.dan mustBe someDan
        addressDetailsForm.addressLine1 mustBe addressLine1
        addressDetailsForm.addressLine2 mustBe Some(addressLine2)
        addressDetailsForm.addressLine3 mustBe Some(addressLine3)
        addressDetailsForm.addressLine4 mustBe Some(addressLine4)
        addressDetailsForm.postCode mustBe Some(postCode)
        addressDetailsForm.countryCode mustBe countryCode
        addressDetailsForm.countryName mustBe Some(countryName)
        addressDetailsForm.isNiAccount mustBe false
      }
    }

    "generates the errors" when {

      "all required values are not provided" in {
        val inValidForm: Form[EditAddressDetailsUserAnswers] =
          new EditAddressDetailsFormProvider()
            .apply()
            .bind(
              Map(
                "postCode"    -> postCode,
                "countryCode" -> countryCode,
                "countryName" -> countryName
              )
            )

        inValidForm.hasErrors mustBe true

        inValidForm.error("dan") mustBe Some(FormError("dan", List("error.required"), List()))
        inValidForm.error("addressLine1") mustBe Some(FormError("addressLine1", List("error.required"), List()))
      }

      "all required values are provide but with incorrect country code" in {
        val inValidForm: Form[EditAddressDetailsUserAnswers] =
          new EditAddressDetailsFormProvider()
            .apply()
            .bind(
              Map(
                "dan"          -> someDan,
                "addressLine1" -> addressLine1,
                "postCode"     -> postCode,
                "countryCode"  -> emptyString,
                "countryName"  -> countryName
              )
            )

        inValidForm.hasErrors mustBe true

        inValidForm.error("countryCode") mustBe Some(
          FormError("countryCode", List("accountDetails.edit.address.country.invalid"), List())
        )
      }
    }
  }
}
