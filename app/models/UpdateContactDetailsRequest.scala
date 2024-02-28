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

import play.api.libs.json.{Json, OFormat}

case class UpdateContactDetailsRequest(dan: String,
                                       eori: String,
                                       name: Option[String],
                                       addressLine1: String,
                                       addressLine2: Option[String],
                                       addressLine3: Option[String],
                                       addressLine4: Option[String],
                                       postCode: Option[String],
                                       countryCode: Option[String],
                                       telephone: Option[String],
                                       fax: Option[String],
                                       email: Option[String])

object UpdateContactDetailsRequest {

  private def trimEmptyPostCode(postcode: Option[String]): Option[String] = postcode match {
    case Some(p) if p.isEmpty => None
    case _ => postcode
  }

  def apply(dan: String, eori: String, amendAddress: ContactDetailsUserAnswers): UpdateContactDetailsRequest = {
    UpdateContactDetailsRequest(
      dan,
      eori,
      amendAddress.name,
      amendAddress.addressLine1,
      amendAddress.addressLine2,
      amendAddress.addressLine3,
      amendAddress.addressLine4,
      trimEmptyPostCode(amendAddress.postCode),
      Some(amendAddress.countryCode),
      amendAddress.telephone,
      amendAddress.fax,
      amendAddress.email
    )
  }

  implicit val formats: OFormat[UpdateContactDetailsRequest] = Json.format[UpdateContactDetailsRequest]
}
