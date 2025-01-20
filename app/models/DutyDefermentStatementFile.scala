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

import models.DDStatementType.{Excise, Supplementary, ExciseDeferment, DutyDeferment}
import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import views.helpers.Formatters

import java.time.LocalDate

case class DutyDefermentStatementFile(
  filename: String,
  downloadURL: String,
  fileSize: Long,
  metadata: DutyDefermentStatementFileMetadata
) extends Ordered[DutyDefermentStatementFile] {

  val fileFormat: FileFormat  = metadata.fileFormat
  val monthAndYear: LocalDate = LocalDate.of(metadata.periodStartYear, metadata.periodStartMonth, 1)

  def downloadLinkAriaLabel()(implicit messages: Messages): String = {
    lazy val endDateMonthAndYear    = Formatters.dateAsMonthAndYear(endDate)
    lazy val endDateDayMonthAndYear = Formatters.dateAsDayMonthAndYear(endDate)
    lazy val startDateDay           = Formatters.dateAsDay(startDate)
    lazy val formattedFileSize      = Formatters.fileSize(fileSize)

    metadata.defermentStatementType match {
      case Supplementary      =>
        messages("cf.account.detail.supplementary-download-link", fileFormat, endDateMonthAndYear, formattedFileSize)
      case Excise             =>
        messages("cf.account.detail.excise-download-link", fileFormat, endDateMonthAndYear, formattedFileSize)
      case ExciseDeferment    =>
        messages("cf.account.detail.download.excise-deferment-1920", fileFormat, endDateMonthAndYear, formattedFileSize)
      case DutyDeferment      =>
        messages("cf.account.detail.download.duty-deferment-1720", fileFormat, endDateMonthAndYear, formattedFileSize)
      case _                  =>
        messages("cf.account.detail.download-link", fileFormat, startDateDay, endDateDayMonthAndYear, formattedFileSize)
    }
  }

  def compare(that: DutyDefermentStatementFile): Int = fileFormat.compare(that.fileFormat)

  val startDate: LocalDate = LocalDate.of(metadata.periodStartYear, metadata.periodStartMonth, metadata.periodStartDay)
  val endDate: LocalDate   = LocalDate.of(metadata.periodEndYear, metadata.periodEndMonth, metadata.periodEndDay)
}

object DutyDefermentStatementFile {
  implicit val format: OFormat[DutyDefermentStatementFile] = Json.format[DutyDefermentStatementFile]
}
