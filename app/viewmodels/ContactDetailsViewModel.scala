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

package viewmodels

import models.responses.retrieve.ContactDetails
import play.api.libs.json.{Json, OFormat}
import utils.Utils.emptyString

case class ContactDetailsViewModel(dan: String,
                                   name: Option[String],
                                   addressLine1: String,
                                   addressLine2: Option[String],
                                   addressLine3: Option[String],
                                   addressLine4: Option[String],
                                   postCode: Option[String],
                                   countryCode: String,
                                   countryName: String,
                                   telephone: Option[String],
                                   fax: Option[String],
                                   email: Option[String])

object ContactDetailsViewModel {

  implicit val formatsDutyDefermentAccountDetails: OFormat[ContactDetailsViewModel] = Json.format[ContactDetailsViewModel]

  def apply(dan: String,
            contactDetails: ContactDetails,
            countryNameF: String => Option[String]): ContactDetailsViewModel = {
    ContactDetailsViewModel(
      dan,
      contactDetails.contactName,
      contactDetails.addressLine1,
      contactDetails.addressLine2,
      contactDetails.addressLine3,
      contactDetails.addressLine4,
      contactDetails.postCode,
      contactDetails.countryCode,
      countryNameF(contactDetails.countryCode).getOrElse(emptyString),
      contactDetails.telephone,
      contactDetails.faxNumber,
      contactDetails.email
    )
  }
}
