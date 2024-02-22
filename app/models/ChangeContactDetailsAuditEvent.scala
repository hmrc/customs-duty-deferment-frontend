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

case class ChangeContactDetailsAuditEvent(dutyDefermentAccountNumber: String,
                                            eori: String,
                                            from: ContactDetails,
                                            to: ContactDetails)

object ChangeContactDetailsAuditEvent {

  def apply(eori: String, from: ContactDetails, to: ContactDetailsUserAnswers): ChangeContactDetailsAuditEvent = {
    ChangeContactDetailsAuditEvent(
      to.dan,
      eori,
      ContactDetails(
        from.contactName,
        from.addressLine1,
        from.addressLine2,
        from.addressLine3,
        from.addressLine4,
        from.postCode,
        from.countryCode,
        from.telephone,
        from.faxNumber,
        from.email
      ),
      ContactDetails(
        to.name,
        to.addressLine1,
        to.addressLine2,
        to.addressLine3,
        to.addressLine4,
        to.postCode,
        to.countryCode,
        to.telephone,
        to.fax,
        to.email
      )
    )
  }

  implicit val formats: OFormat[ChangeContactDetailsAuditEvent] = Json.format[ChangeContactDetailsAuditEvent]
}
