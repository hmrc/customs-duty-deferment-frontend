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

import models.DDStatementType._
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
        case _                => emptyString
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
      DDStatementType("DD1920") mustBe ExciseDeferment
      DDStatementType("DD1720") mustBe DutyDeferment
      DDStatementType("UNKNOWN STATEMENT TYPE") mustBe UnknownStatementType
    }

    "return correct value for unapply method" in new Setup {

      private def getDDSttTypeName(ddSttType: DDStatementType) =
        ddSttType match {
          case DDStatementType(name) => name
          case _                     => emptyString
        }

      getDDSttTypeName(exciseSttType) mustBe "Excise"
      getDDSttTypeName(supplementarySttType) mustBe "Supplementary"
      getDDSttTypeName(exciseDefermentSttType) mustBe "DD1920"
      getDDSttTypeName(dutyDefermentSttType) mustBe "DD1720"
      getDDSttTypeName(weeklySttType) mustBe "Weekly"
    }

    "provide correct output while comparing" in new Setup {
      List(exciseSttType, supplementarySttType).min.name mustBe "Excise"
      List(exciseDefermentSttType, dutyDefermentSttType).min.name mustBe "DD1920"
      List(
        exciseSttType,
        supplementarySttType,
        exciseDefermentSttType,
        dutyDefermentSttType,
        weeklySttType
      ).min.name mustBe "DD1920"
    }

    "return correct value for the Reads" in new Setup {
      import DDStatementType.format

      Json.fromJson(JsString("Excise")) mustBe JsSuccess(exciseSttType)
    }

    "return correct value for the Writes" in new Setup {
      Json.toJson(exciseSttType) mustBe JsString("Excise")
    }
  }

  trait Setup {
    val exciseSttType: DDStatementType          = DDStatementType("Excise")
    val supplementarySttType: DDStatementType   = DDStatementType("Supplementary")
    val exciseDefermentSttType: DDStatementType = DDStatementType("DD1920")
    val dutyDefermentSttType: DDStatementType   = DDStatementType("DD1720")
    val weeklySttType: DDStatementType          = DDStatementType("Weekly")
    val pdfFileFormat: FileFormat               = FileFormat("PDF")
    val pdfJsValue: JsString                    = JsString("PDF")

    val fileSize = 10L

    val ddStatementFile1: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          Weekly,
          Some(true),
          Some("BACS"),
          "123456",
          None
        )
      )

    val ddStatementFile2: DutyDefermentStatementFile =
      DutyDefermentStatementFile(
        "someFilename",
        "downloadUrl",
        fileSize,
        DutyDefermentStatementFileMetadata(
          periodStartYear2023,
          periodStartMonth10,
          periodStartDay1,
          periodEndYear2023,
          periodEndMonth10,
          periodEndDay8,
          FileFormat.Csv,
          DutyDefermentStatement,
          Weekly,
          Some(true),
          Some("BACS"),
          "123456",
          None
        )
      )

    val ddStatementFileList: Seq[DutyDefermentStatementFile] = Seq(ddStatementFile1, ddStatementFile2)
  }
}
