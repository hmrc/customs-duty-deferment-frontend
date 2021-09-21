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

import play.api.libs.json._
import play.api.{Logger, LoggerLike}

import scala.collection.immutable.SortedSet

sealed abstract class FileFormat(val name: String) extends Ordered[FileFormat] {
  val order: Int

  def compare(that: FileFormat): Int = order.compare(that.order)

  override def toString: String = name
}

object FileFormat {

  case object Pdf extends FileFormat("PDF") {
    val order = 1
  }

  case object Csv extends FileFormat("CSV") {
    val order = 2
  }

  case object UnknownFileFormat extends FileFormat("UNKNOWN FILE FORMAT") {
    val order = 99
  }

  val log: LoggerLike = Logger(this.getClass)

  val SdesFileFormats: SortedSet[FileFormat] = SortedSet(Pdf, Csv)
  val PvatFileFormats: SortedSet[FileFormat] = SortedSet(Pdf)

  def filterFileFormats(allowedFileFormats: SortedSet[FileFormat])(files: Seq[DutyDefermentStatementFile]): Seq[DutyDefermentStatementFile] =
    files.filter(file => allowedFileFormats(file.metadata.fileFormat))

  def apply(name: String): FileFormat = name.toUpperCase match {
    case Pdf.name => Pdf
    case Csv.name => Csv
    case _ =>
      log.warn(s"Unknown file format: $name")
      UnknownFileFormat
  }

  def unapply(arg: FileFormat): Option[String] = Some(arg.name)

  implicit val fileFormatFormat: Format[FileFormat] = new Format[FileFormat] {
    def reads(json: JsValue) = JsSuccess(apply(json.as[String]))

    def writes(obj: FileFormat) = JsString(obj.name)
  }
}

sealed abstract class DDStatementType(val name: String) extends Ordered[DDStatementType] {
  val order: Int

  def compare(that: DDStatementType): Int = order.compare(that.order)
}

object DDStatementType {
  val log: LoggerLike = Logger(this.getClass)

  case object Excise extends DDStatementType("Excise") {
    val order = 1
  }

  case object Supplementary extends DDStatementType(name = "Supplementary") {
    val order = 2
  }

  case object Weekly extends DDStatementType("Weekly") {
    val order = 3
  }

  case object UnknownStatementType extends DDStatementType("UNKNOWN STATEMENT TYPE") {
    val order = 4
  }

  def apply(name: String): DDStatementType = name match {
    case Weekly.name => Weekly
    case Supplementary.name => Supplementary
    case Excise.name => Excise
    case _ =>
      log.warn(s"Unknown duty deferment statement type: $name")
      UnknownStatementType
  }

  def unapply(arg: DDStatementType): Option[String] = Some(arg.name)

  implicit val format: Format[DDStatementType] = new Format[DDStatementType] {
    override def reads(json: JsValue): JsResult[DDStatementType] =
      json.validate[String].map(apply)

    override def writes(o: DDStatementType): JsValue =
      JsString(o.name)
  }
}