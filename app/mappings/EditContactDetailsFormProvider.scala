/*
 * Copyright 2021 HM Revenue & Customs
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


import mappings.ConditionalMappingsExt.mandatoryIfNotExists
import models.ContactDetailsUserAnswers
import play.api.data.Forms.{mapping, of, optional}
import play.api.data.format.Formats._
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.data.{Form, FormError, Forms}
import services.CountriesProviderService

import javax.inject.Inject


class EditContactDetailsFormProvider @Inject()(
                                                countriesProviderService: CountriesProviderService
                                              ) extends Constraints {

  def apply(): Form[ContactDetailsUserAnswers] = {
    // A hidden form component tracks countryName and is updated using JS.
    // if JS is not enabled it defaults to countryNameNoJs, and this check is not performed - as they just select from a dropdown.
    Form(
      mapping(
        "dan" -> of[String],
        "name" -> optional(Forms.text).verifying(isValidNameField("accountDetails.edit.name")),
        "addressLine1" -> of[String].verifying(validMandatoryAddressField("accountDetails.edit.address.line1")),
        "addressLine2" -> optional(Forms.text).verifying(validOptionalAddressField("accountDetails.edit.address.line2")),
        "addressLine3" -> optional(Forms.text).verifying(validOptionalAddressField("accountDetails.edit.address.line3")),
        "addressLine4" -> optional(Forms.text).verifying(validOptionalAddressField("accountDetails.edit.address.line4")),
        "postCode" -> postcodeMapping,
        "countryCode" -> of[String].verifying(isValidCountryCode),
        "countryName" -> mandatoryIfNotExists("countryNameNoJs", of[String].verifying(isValidCountryName)),
        "telephone" -> optional(Forms.text).verifying(isValidPhoneNumber("accountDetails.edit.telephone")),
        "fax" -> optional(Forms.text).verifying(isValidPhoneNumber("accountDetails.edit.fax")),
        "email" -> optional(Forms.text).verifying(isValidEmail)
      )(ContactDetailsUserAnswers.apply)(ContactDetailsUserAnswers.unapply)
    )
  }

  private val isValidCountryName: Constraint[Any] = Constraint({
    case country: String if countriesProviderService.isValidCountryName(country) => Valid
    case _ => Invalid(countryError)
  })

  def toForm(formData: Map[String, Seq[String]], dan: String): Form[ContactDetailsUserAnswers] = {
    val newDetailsFormData: Map[String, Seq[String]] = formData + ("dan" -> Seq(dan))
    deDuplicateCountryErrors(apply().bindFromRequest(newDetailsFormData))
  }


  // If there are countryCode and countryName errors - this combines them
  private def deDuplicateCountryErrors(form: Form[ContactDetailsUserAnswers]): Form[ContactDetailsUserAnswers] = {
    form.copy(errors = form.errors
      .map {
        case countryNameError: FormError if countryNameError.key == "countryName" => countryNameError.copy(key = "countryCode")
        case x: FormError => x
      }
      .distinct
    )
  }
}


