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
import play.api.data.Forms.{mapping, of, optional}
import play.api.data.format.Formats.*
import play.api.data.validation.Constraint
import play.api.data.{Form, Forms}

import javax.inject.Inject

class EditAddressDetailsFormProvider @Inject() extends Constraints {

  def apply(): Form[EditAddressDetailsUserAnswers] =
    Form(
      mapping(
        "dan"          -> of[String],
        "addressLine1" -> of[String].verifying(validMandatoryAddressField("accountDetails.edit.address.line1")),
        "addressLine2" -> optional(Forms.text)
          .verifying(validOptionalAddressField("accountDetails.edit.address.line2")),
        "addressLine3" -> optional(Forms.text)
          .verifying(validOptionalAddressField("accountDetails.edit.address.line3")),
        "addressLine4" -> optional(Forms.text)
          .verifying(validOptionalAddressField("accountDetails.edit.address.line4")),
        "postCode"     -> postcodeMapping,
        "countryCode"  -> of[String].verifying(isValidCountryCode),
        "countryName"  -> optional(of[String]),
        "isNiAccount"  -> of[Boolean]
      )(EditAddressDetailsUserAnswers.apply)(ead => Some(Tuple.fromProductTyped(ead)))
    )

  def toForm(formData: Map[String, Seq[String]], dan: String): Form[EditAddressDetailsUserAnswers] = {

    val newDetailsFormData: Map[String, Seq[String]] = formData + ("dan" -> Seq(dan))

    apply().bindFromRequest(newDetailsFormData)
  }
}
