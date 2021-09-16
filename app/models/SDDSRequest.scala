package models

import play.api.libs.json.{Json, OFormat}

case class SDDSRequest(returnUrl: String, backUrl: String, dan: String, email: String)

object SDDSRequest {
  implicit val format: OFormat[SDDSRequest] = Json.format[SDDSRequest]
}