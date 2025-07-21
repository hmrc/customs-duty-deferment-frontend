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

import play.api.libs.json.{JsSuccess, Json}
import util.SpecBase

class EmailVerifiedResponseSpec extends SpecBase {

  "EmailVerifiedResponse.format" should {
    "generate correct output for Json Reads" in new Setup {
      import EmailVerifiedResponse.format

      Json.fromJson(Json.parse(emailVerifiedResJsString)) mustBe JsSuccess(emailVerifiedResOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(emailVerifiedResOb) mustBe Json.parse(emailVerifiedResJsString)
    }
  }

  "EmailUnverifiedResponse.format" should {
    "generate correct output for Json Reads" in new Setup {

      import EmailUnverifiedResponse.format

      Json.fromJson(Json.parse(emailUnverifiedResJsString)) mustBe JsSuccess(emailUnverifiedResOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(emailUnverifiedResOb) mustBe Json.parse(emailUnverifiedResJsString)
    }
  }

  trait Setup {
    val emailVerifiedResOb: EmailVerifiedResponse     = EmailVerifiedResponse(Some(emailId))
    val emailUnverifiedResOb: EmailUnverifiedResponse = EmailUnverifiedResponse(Some(emailId))

    val emailVerifiedResJsString: String   = """{"verifiedEmail":"test@test.com"}""".stripMargin
    val emailUnverifiedResJsString: String = """{"unVerifiedEmail":"test@test.com"}""".stripMargin
  }
}
