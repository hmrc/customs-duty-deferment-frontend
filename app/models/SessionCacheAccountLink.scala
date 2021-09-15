package models

import play.api.libs.json.{Json, OFormat}

case class SessionCacheAccountLink(eori: String,
                                   accountNumber: String,
                                   accountStatus: CDSAccountStatus,
                                   accountStatusId: Option[CDSAccountStatusId],
                                   linkId: String)

object SessionCacheAccountLink {
  implicit val format: OFormat[SessionCacheAccountLink] = Json.format[SessionCacheAccountLink]

}
