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
import play.api.libs.json.{Json, OFormat}

case class EditAddressDetailsUserAnswers(
  dan: String,
  addressLine1: String,
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  postCode: Option[String],
  countryCode: String,
  countryName: Option[String],
  isNiAccount: Boolean
) {

  def toContactDetailsUserAnswers(
    initialContactDetails: ContactDetails,
    isNiAccount: Boolean
  ): ContactDetailsUserAnswers =
    ContactDetailsUserAnswers(
      dan = dan,
      name = initialContactDetails.contactName,
      addressLine1 = addressLine1,
      addressLine2 = addressLine2,
      addressLine3 = addressLine3,
      addressLine4 = addressLine4,
      postCode = postCode,
      countryCode = countryCode,
      countryName = countryName,
      telephone = initialContactDetails.telephone,
      fax = initialContactDetails.faxNumber,
      email = initialContactDetails.email,
      isNiAccount = isNiAccount
    )
}

object EditAddressDetailsUserAnswers {
  implicit val formats: OFormat[EditAddressDetailsUserAnswers] = Json.format[EditAddressDetailsUserAnswers]
}
