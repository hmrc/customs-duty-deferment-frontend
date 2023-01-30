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

import play.api.libs.json._


case class Metadata(items: Seq[MetadataItem]) {
  def toDutyDefermentStatementFileMetadata: DutyDefermentStatementFileMetadata = {
    val metadata = items.map(item => (item.key, item.value)).toMap

    def mapDutyOverLimit: Boolean = {
      metadata.getOrElse("DutyOverLimit", "false") match {
        case "Y" => true
        case _ => false
      }
    }

    DutyDefermentStatementFileMetadata(
      metadata("PeriodStartYear").toInt,
      metadata("PeriodStartMonth").toInt,
      metadata("PeriodStartDay").toInt,
      metadata("PeriodEndYear").toInt,
      metadata("PeriodEndMonth").toInt,
      metadata("PeriodEndDay").toInt,
      FileFormat(metadata("FileType")),
      FileRole(metadata("FileRole")),
      DDStatementType(metadata("DefermentStatementType")),
      Some(mapDutyOverLimit),
      Some(metadata.getOrElse("DutyPaymentType", "Unknown")),
      metadata.getOrElse("DAN", "Unknown"),
      metadata.get("statementRequestID"))
  }
}

object Metadata {
  implicit val metadataReads: Reads[Metadata] = __.read[List[MetadataItem]].map(Metadata.apply)
  implicit val metadataWrites: Writes[Metadata] = (o: Metadata) => JsArray(o.items.map(
    item => Json.obj(("metadata", item.key), ("value", item.value))))
}