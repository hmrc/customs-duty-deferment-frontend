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

import play.api.libs.json.{JsString, Json}
import util.SpecBase

class CdsAccountStatusSpec extends SpecBase {

  "CdsAccountStatus" should {
    "read/write the correct values" in {
      JsString("open").as[CDSAccountStatus] mustBe AccountStatusOpen
      JsString("closed").as[CDSAccountStatus] mustBe AccountStatusClosed
      JsString("suspended").as[CDSAccountStatus] mustBe AccountStatusSuspended
      JsString("pending").as[CDSAccountStatus] mustBe AccountStatusPending
      JsString("unknown").as[CDSAccountStatus] mustBe AccountStatusOpen

      val open: CDSAccountStatus = AccountStatusOpen
      Json.toJson(open) mustBe JsString("open")
      val closed: CDSAccountStatus = AccountStatusClosed
      Json.toJson(closed) mustBe JsString("closed")
      val suspended: CDSAccountStatus = AccountStatusSuspended
      Json.toJson(suspended) mustBe JsString("suspended")
      val pending: CDSAccountStatus = AccountStatusPending
      Json.toJson(pending) mustBe JsString("pending")
    }
  }
}
