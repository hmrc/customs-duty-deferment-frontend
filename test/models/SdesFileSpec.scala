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

import models.DDStatementType.{Excise, Supplementary, UnknownStatementType, Weekly}
import models.FileFormat.{Csv, Pdf, PvatFileFormats, SdesFileFormats, UnknownFileFormat}
import models.FileRole.DutyDefermentStatement
import play.api.libs.json.{JsString, JsSuccess, Json}
import util.SpecBase
import utils.Utils.emptyString

import scala.collection.immutable.SortedSet

class SdesFileSpec extends SpecBase {

  "FileFormat" should {
    "return correct value for apply method" in new Setup {
      FileFormat("PDF") mustBe Pdf
      FileFormat("CSV") mustBe Csv
      FileFormat("UNKNOWN FILE FORMAT") mustBe UnknownFileFormat
    }

    "return correct value for unapply method" in new Setup {
      val pdfFileFormatName: String = FileFormat("PDF") match {
        case FileFormat(name) => name
        case _ => emptyString
      }

      pdfFileFormatName mustBe "PDF"
    }

    "return correct value for Reads" in new Setup {
      import FileFormat.fileFormatFormat

      Json.fromJson(pdfJsValue) mustBe JsSuccess(pdfFileFormat)
    }

    "return correct value for Writes" in new Setup {
      Json.toJson(pdfFileFormat) mustBe pdfJsValue
    }

    "return correct output for filterFileFormats" in new Setup {
      FileFormat.filterFileFormats(SdesFileFormats)(ddStatementFileList) mustBe ddStatementFileList
    }

    "return correct value for PvatFileFormats" in new Setup {
      PvatFileFormats mustBe SortedSet(Pdf)
    }
  }

  "DDStatementType" should {
    "return correct value for apply method" in new Setup {
      DDStatementType("Excise") mustBe Excise
      DDStatementType("Supplementary") mustBe Supplementary
      DDStatementType("Weekly") mustBe Weekly
      DDStatementType("UNKNOWN STATEMENT TYPE") mustBe UnknownStatementType
    }

    "return correct value for unapply method" in new Setup {
      val exciseTypeName: String = DDStatementType("Excise") match {
        case DDStatementType(name) => name
        case _ => emptyString
      }

      exciseTypeName mustBe "Excise"
    }

    "provide correct output while comparing" in new Setup {
      List(DDStatementType("Excise"), DDStatementType("Supplementary")).min.name mustBe
        "Excise"
    }

    "return correct value for the Reads" in new Setup {
      import DDStatementType.format

      Json.fromJson(JsString("Excise")) mustBe JsSuccess(exciseStatement)
    }

    "return correct value for the Writes" in new Setup {
      Json.toJson(exciseStatement) mustBe JsString("Excise")
    }
  }

  trait Setup {
    val exciseStatement: DDStatementType = DDStatementType("Excise")
    val pdfFileFormat: FileFormat = FileFormat("PDF")
    val pdfJsValue: JsString = JsString("PDF")

    val ddStatementFile1: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        10L,
        DutyDefermentStatementFileMetadata(2018,
          6,
          1,
          2018,
          6,
          8,
          FileFormat.Csv,
          DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", None)
      )

    val ddStatementFile2: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        10L,
        DutyDefermentStatementFileMetadata(2018,
          6,
          1,
          2018,
          6,
          8,
          FileFormat.Csv,
          DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", None)
      )

    val ddStatementFileList: Seq[DutyDefermentStatementFile] = Seq(ddStatementFile1, ddStatementFile2)
  }
}
