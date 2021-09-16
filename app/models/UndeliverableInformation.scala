package models

import play.api.libs.json.{Json, OFormat}

case class UndeliverableInformation(subject: String)

object UndeliverableInformation {
  implicit val format: OFormat[UndeliverableInformation] = Json.format[UndeliverableInformation]
}

