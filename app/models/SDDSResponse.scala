package models

import play.api.libs.json.{Json, OFormat}

case class SDDSResponse(nextUrl: String)

object SDDSResponse {
  implicit val format: OFormat[SDDSResponse] = Json.format[SDDSResponse]
}
