/*
 * Copyright 2021 HM Revenue & Customs
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

case class DutyDefermentStatementFileMetadata(periodStartYear: Int,
                                              periodStartMonth: Int,
                                              periodStartDay: Int,
                                              periodEndYear: Int,
                                              periodEndMonth: Int,
                                              periodEndDay: Int,
                                              fileFormat: FileFormat,
                                              fileRole: FileRole,
                                              defermentStatementType: DDStatementType,
                                              dutyOverLimit: Option[Boolean],
                                              dutyPaymentType: Option[String],
                                              dan: String,
                                              statementRequestId: Option[String]) {
  def toMap: Map[String, String] = {
    val fieldNames: Seq[String] = getClass.getDeclaredFields.map(_.getName)
    val fieldValues: Seq[String] = productIterator.to.map(_.toString)
    fieldNames.zip(fieldValues).toMap
  }
}

object DutyDefermentStatementFileMetadata {
  implicit val format: OFormat[DutyDefermentStatementFileMetadata] = Json.format[DutyDefermentStatementFileMetadata]
}