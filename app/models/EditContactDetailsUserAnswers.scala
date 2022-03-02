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

  def toContactDetailsUserAnswers(initialContactDetails: ContactDetails,
                                  getCountryNameF: String => Option[String]): ContactDetailsUserAnswers = {
    ContactDetailsUserAnswers(
      dan = dan,
      name = name,
      addressLine1 = initialContactDetails.addressLine1,
      addressLine2 = initialContactDetails.addressLine2,
      addressLine3 = initialContactDetails.addressLine3,
      addressLine4 = initialContactDetails.addressLine4,
      postCode = initialContactDetails.postCode,
      countryCode = initialContactDetails.countryCode,
      countryName = getCountryNameF(initialContactDetails.countryCode),
      telephone = telephone,
      fax = fax,
      email = email)
    // fix country name?
  }
}

object EditContactDetailsUserAnswers {

  implicit val formats: OFormat[EditContactDetailsUserAnswers] = Json.format[EditContactDetailsUserAnswers]
}

