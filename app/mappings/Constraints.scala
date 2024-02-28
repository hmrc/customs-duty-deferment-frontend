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

import play.api.data.validation._
import play.api.data.{Forms, Mapping}
import uk.gov.voa.play.form.ConditionalMappings.isAnyOf
import uk.gov.voa.play.form.MandatoryOptionalMapping
import utils.Utils.emptyString

import scala.util.matching.Regex

trait Constraints {
  private val isValidAddressFieldRegex = """^[A-Za-z0-9\s\-,.&'\/()!]+$""".r
  private val invalidPhoneNumberCharsRegex = """[^\d|^\s|^+]+""".r
  private val postCodeMandatoryCountryCodes = Seq("GB", "GG", "JE", "IM")
  private val addressLengthLimit = 35
  private val nameLengthLimit = 50
  private val isValidPostCodeRegex: Regex = ("^(?i)(GIR 0AA)|((([A-Z][0-9][0-9]?)|(([A-Z][A-HJ-Y][0-9][0-9]?)" +
    "|(([A-Z][0-9][A-Z])|([A-Z][A-HJ-Y][0-9]?[A-Z])))) ?[0-9][A-Z]{2})$").r
  private val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r
  val countryError: ValidationError = ValidationError("accountDetails.edit.address.country.invalid")

  def validPostcode: Constraint[String] =
    Constraint({
      case s if s.trim.length > 8 => Invalid(ValidationError("accountDetails.edit.postcode.max"))
      case s if s.trim.isEmpty => Invalid(ValidationError("accountDetails.edit.postcode.empty"))
      case s if s.trim.matches(isValidPostCodeRegex.regex) => Valid
      case _ => Invalid(ValidationError("accountDetails.edit.postcode.invalid"))
    })

  def postcodeMapping: Mapping[Option[String]] =
    ConditionalMapping(
      condition = isAnyOf("countryCode", postCodeMandatoryCountryCodes),
      wrapped = MandatoryOptionalMapping(Forms.text.verifying(validPostcode)),
      elseValue = (key, data) => data.get(key)
    )

  def isValidPhoneNumber(message: String): Constraint[Any] = Constraint({
    case Some(number: String) if invalidPhoneNumberCharsRegex.findFirstIn(
      number.trim).nonEmpty || number.trim.length > 24 => Invalid(ValidationError(s"$message.invalid"))
    case _ => Valid
  })

  val isValidCountryCode: Constraint[Any] = Constraint({
    case country: String if country.trim.nonEmpty => Valid
    case _ => Invalid(countryError)
  })

  def stripWhiteSpaces(str: String): String = str.trim.replaceAll("\\s", emptyString)

  def isValid(e: String): Boolean = e match {
    case e if emailRegex.findFirstMatchIn(e).isDefined => true
    case _ => false
  }

  def isValidEmail: Constraint[Option[String]] =
    Constraint {
      case None => Invalid(ValidationError("emailAddress.edit.empty"))
      case Some(email: String) if Option(email).isEmpty => Invalid(ValidationError("emailAddress.edit.empty"))
      case Some(email: String) if stripWhiteSpaces(email).isEmpty => Invalid(ValidationError("emailAddress.edit.empty"))
      case Some(email: String) if stripWhiteSpaces(email).length > 132 => Invalid(
        ValidationError("emailAddress.edit.too-long"))
      case Some(email: String) if !isValid(stripWhiteSpaces(email)) => Invalid(
        ValidationError("emailAddress.edit.wrong-format"))
      case _ => Valid
    }

  def isValidNameField(message: String): Constraint[Option[String]] = {
    Constraint {
      case None => Invalid(ValidationError(s"$message.empty"))
      case Some(field: String) if field.trim.isEmpty => Invalid(ValidationError(s"$message.empty"))
      case Some(field: String) if field.trim.length > nameLengthLimit => Invalid(ValidationError(s"$message.max"))
      case Some(field: String) if !field.matches(isValidAddressFieldRegex.regex) => Invalid(
        ValidationError(s"$message.invalid"))
      case _ => Valid
    }
  }

  def validOptionalAddressField(message: String): Constraint[Option[String]] = {
    Constraint {
      case Some(field: String) if field.trim.length > addressLengthLimit => Invalid(ValidationError(s"$message.max"))
      case Some(field: String) if !field.matches(isValidAddressFieldRegex.regex) => Invalid(
        ValidationError(s"$message.invalid"))
      case _ => Valid
    }
  }

  def validMandatoryAddressField(message: String): Constraint[String] = {
    Constraint {
      case field: String if field.trim.isEmpty => Invalid(ValidationError(s"$message.empty"))
      case field: String if field.trim.length > addressLengthLimit => Invalid(ValidationError(s"$message.max"))
      case field: String if !field.matches(isValidAddressFieldRegex.regex) => Invalid(
        ValidationError(s"$message.invalid"))
      case _ => Valid
    }
  }
}
