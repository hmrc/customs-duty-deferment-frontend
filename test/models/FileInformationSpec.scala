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

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class FileInformationSpec extends SpecBase {
  "Json Reads" should {

    "generate correct output" in new Setup {
      import FileInformation.fileInformationFormats

      Json.fromJson(Json.parse(fileInformationJsString)) mustBe JsSuccess(fileInformationOb)
    }
  }

  "Json Writes" should {

    "generate correct output" in new Setup {
      Json.toJson(fileInformationOb) mustBe Json.parse(fileInformationJsString)
    }
  }

  "Invalid JSON" should {
    "fail" in {
      val invalidJson = "{ \"eori1\": \"testEori1\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[FileInformation]
      }
    }
  }

  trait Setup {
    val fileInformationOb: FileInformation = FileInformation(
      filename = fileName,
      downloadURL = testHref,
      fileSize = fileSizeData,
      metadata = Metadata(Seq(MetadataItem(testKey, testKeyValue)))
    )

    val fileInformationJsString: String =
      """{"filename":"test_file_name",
        |"downloadURL":"http://www.test.com",
        |"fileSize":10,
        |"metadata":[{"metadata":"test_key","value":"test_key_value"}]
        |}""".stripMargin
  }
}
