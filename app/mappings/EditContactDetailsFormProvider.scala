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


import javax.inject.Inject
import models.EditContactDetailsUserAnswers
import play.api.data.Forms.{mapping, of, optional}
import play.api.data.format.Formats._
import play.api.data.{Form, FormError, Forms}

class EditContactDetailsFormProvider @Inject()() extends Constraints {

  def apply(): Form[EditContactDetailsUserAnswers] = {

    Form(
      mapping(
        "dan" -> of[String],
        "name" -> optional(Forms.text).verifying(isValidNameField("accountDetails.edit.name")),
        "telephone" -> optional(Forms.text).verifying(isValidPhoneNumber("accountDetails.edit.telephone")),
        "fax" -> optional(Forms.text).verifying(isValidPhoneNumber("accountDetails.edit.fax")),
        "email" -> optional(Forms.text).verifying(isValidEmail),
        "isNiAccount" -> of[Boolean]
      )(EditContactDetailsUserAnswers.apply)(ecd => Some(Tuple.fromProductTyped(ecd)))
    )
  }

  def toForm(formData: Map[String, Seq[String]], dan: String): Form[EditContactDetailsUserAnswers] = {
    val newDetailsFormData: Map[String, Seq[String]] = formData + ("dan" -> Seq(dan))
    deDuplicateCountryErrors(apply().bindFromRequest(newDetailsFormData))
  }

  private def deDuplicateCountryErrors(form: Form[EditContactDetailsUserAnswers]): Form[EditContactDetailsUserAnswers] = {
    form.copy(errors = form.errors
      .map {
        case countryNameError: FormError if countryNameError.key == "countryName" =>
          countryNameError.copy(key = "countryCode")

        case x: FormError => x
      }
      .distinct
    )
  }
}
