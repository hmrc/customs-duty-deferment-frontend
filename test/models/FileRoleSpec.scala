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

import models.FileRole.DutyDefermentStatement
import play.api.libs.json.JsSuccess
import util.SpecBase

class FileRoleSpec extends SpecBase {

  "FileRole" should {
    "FileRole successfully applies dutyDeferementStatement" in {
      val reads: JsSuccess[FileRole] = JsSuccess(FileRole.apply("DutyDefermentStatement"))
      reads mustBe JsSuccess(DutyDefermentStatement)
    }

    "throw Exception when an unknown file role is applied" in {
      assertThrows[java.lang.Exception] {
        val reads: JsSuccess[FileRole] = JsSuccess(FileRole.apply(""))
        reads mustBe "Unknown file role: "
      }
    }

    "FileRole can successfully unapply a fileRole" in {
      val fileRole: FileRole = FileRole.apply("DutyDefermentStatement")
      val result = FileRole.unapply(fileRole)
      result mustBe Some("DutyDefermentStatement")
    }
  }
}


