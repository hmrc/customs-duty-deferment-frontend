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


case class AddressDetailsUserAnswers(dan: String,
                                     addressLine1: String,
                                     addressLine2: Option[String],
                                     addressLine3: Option[String],
                                     addressLine4: Option[String],
                                     postCode: Option[String],
                                     countryCode: String,
                                     countryName: Option[String]) {

  def withWhitespaceTrimmed: AddressDetailsUserAnswers = {
    this.copy(
      addressLine1 = addressLine1.trim,
      addressLine2 = addressLine2.map(_.trim),
      addressLine3 = addressLine3.map(_.trim),
      addressLine4 = addressLine4.map(_.trim),
      postCode = postCode.map(_.trim),
      countryCode = countryCode.trim,
      countryName = countryName.map(_.trim)
    )
  }

}

object AddressDetailsUserAnswers {

  implicit val formats: OFormat[AddressDetailsUserAnswers] = Json.format[AddressDetailsUserAnswers]

  def editAddressDetails(dan: String,
                         contactDetails: ContactDetails,
                         getCountryNameF: String => Option[String]): AddressDetailsUserAnswers = {
    AddressDetailsUserAnswers(
      dan,
      contactDetails.addressLine1,
      contactDetails.addressLine2,
      contactDetails.addressLine3,
      contactDetails.addressLine4,
      contactDetails.postCode,
      contactDetails.countryCode,
      getCountryNameF(contactDetails.countryCode)
    )
  }
}


