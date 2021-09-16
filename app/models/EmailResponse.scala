package models

import play.api.libs.json.{Json, OFormat}

case class EmailResponse(address: Option[String], timestamp: Option[String], undeliverableInformation: Option[UndeliverableInformation])

object EmailResponse {
  implicit val format: OFormat[EmailResponse] = Json.format[EmailResponse]
}