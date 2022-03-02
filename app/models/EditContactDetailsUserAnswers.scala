/*
 * Copyright 2022 HM Revenue & Customs
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

case class EditContactDetailsUserAnswers(dan: String,
                                         name: Option[String],
                                         telephone: Option[String],
                                         fax: Option[String],
                                         email: Option[String]) {

  def withWhitespaceTrimmed: EditContactDetailsUserAnswers = {
    this.copy(
      name = name.map(_.trim),
      telephone = telephone.map(_.trim),
      fax = fax.map(_.trim),
      email = email.map(_.trim)
    )
  }
}

object EditContactDetailsUserAnswers {

  implicit val formats: OFormat[EditContactDetailsUserAnswers] = Json.format[EditContactDetailsUserAnswers]

  def fromContactDetails(dan: String,
                         contactDetails: ContactDetails,
                         getCountryNameF: String => Option[String]): ContactDetailsUserAnswers = {
    ContactDetailsUserAnswers(
      dan,
      contactDetails.contactName,
      contactDetails.addressLine1,
      contactDetails.addressLine2,
      contactDetails.addressLine3,
      contactDetails.addressLine4,
      contactDetails.postCode,
      contactDetails.countryCode,
      getCountryNameF(contactDetails.countryCode),
      contactDetails.telephone,
      contactDetails.faxNumber,
      contactDetails.email
    )
  }

}

