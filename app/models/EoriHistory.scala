package models

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, _}

import scala.util.{Failure, Success, Try}

case class EoriHistory(eori: String,
                       validFrom: Option[LocalDate],
                       validUntil: Option[LocalDate]) {

  val isHistoricEori: Boolean = validUntil.isDefined
}

object EoriHistory {
  val logger = Logger(this.getClass)

  implicit val eoriHistoryFormat: Reads[EoriHistory] = (
    (JsPath \ "eori").read[String] and
      (JsPath \ "validFrom").readNullable[String].map(asDate) and
      (JsPath \ "validUntil").readNullable[String].map(asDate)
    ) (EoriHistory.apply _)

  implicit val eoriHistoryWrites: Writes[EoriHistory] = (o: EoriHistory) => {
    Json.obj(
      "eori" -> o.eori,
      "validFrom" -> o.validFrom.map(_.toString),
      "validUntil" -> o.validUntil.map(_.toString)
    )
  }

  private def asDate(maybeDate: Option[String]): Option[LocalDate] = {
    maybeDate.flatMap(dateString => Try(LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)) match {
      case Success(date) => Some(date)
      case Failure(_) =>
        Try(LocalDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)) match {
          case Success(dateTime) => Some(dateTime.toLocalDate)
          case Failure(ex) =>
            logger.error(ex.getMessage, ex)
            None
        }
    })
  }

}