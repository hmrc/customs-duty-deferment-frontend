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

import play.api.libs.json.{JsNumber, JsString, Json}
import util.SpecBase

class CdsAccountStatusIdSpec extends SpecBase {

  "CdsAccountStatusId" should {
    "read/write the correct values" in {
      JsNumber(0).as[CDSAccountStatusId] mustBe DefermentAccountAvailable
      JsNumber(1).as[CDSAccountStatusId] mustBe ChangeOfLegalEntity
      JsNumber(2).as[CDSAccountStatusId] mustBe GuaranteeCancelledGuarantorsRequest
      JsNumber(3).as[CDSAccountStatusId] mustBe GuaranteeCancelledTradersRequest
      JsNumber(4).as[CDSAccountStatusId] mustBe DirectDebitMandateCancelled
      JsNumber(5).as[CDSAccountStatusId] mustBe DebitRejectedAccountClosedOrTransferred
      JsNumber(6).as[CDSAccountStatusId] mustBe DebitRejectedReferToDrawer
      JsNumber(7).as[CDSAccountStatusId] mustBe ReturnedMailOther
      JsNumber(8).as[CDSAccountStatusId] mustBe GuaranteeExceeded
      JsNumber(9).as[CDSAccountStatusId] mustBe AccountCancelled
      JsNumber(99).as[CDSAccountStatusId] mustBe DefermentAccountAvailable
      JsString("").as[CDSAccountStatusId] mustBe DefermentAccountAvailable

      val defermentAccountAvailable: CDSAccountStatusId = DefermentAccountAvailable
      Json.toJson(defermentAccountAvailable) mustBe JsNumber(0)
      val changeOfLegalEntity: CDSAccountStatusId = ChangeOfLegalEntity
      Json.toJson(changeOfLegalEntity) mustBe JsNumber(1)
      val guaranteeCancelledGuarantorsRequest: CDSAccountStatusId = GuaranteeCancelledGuarantorsRequest
      Json.toJson(guaranteeCancelledGuarantorsRequest) mustBe JsNumber(2)
      val guaranteeCancelledTradersRequest: CDSAccountStatusId = GuaranteeCancelledTradersRequest
      Json.toJson(guaranteeCancelledTradersRequest) mustBe JsNumber(3)
      val directDebitMandateCancelled: CDSAccountStatusId = DirectDebitMandateCancelled
      Json.toJson(directDebitMandateCancelled) mustBe JsNumber(4)
      val debitRejectedAccountClosedOrTransferred: CDSAccountStatusId = DebitRejectedAccountClosedOrTransferred
      Json.toJson(debitRejectedAccountClosedOrTransferred) mustBe JsNumber(5)
      val debitRejectedReferToDrawer: CDSAccountStatusId = DebitRejectedReferToDrawer
      Json.toJson(debitRejectedReferToDrawer) mustBe JsNumber(6)
      val returnedMailOther: CDSAccountStatusId = ReturnedMailOther
      Json.toJson(returnedMailOther) mustBe JsNumber(7)
      val guaranteeExceeded: CDSAccountStatusId = GuaranteeExceeded
      Json.toJson(guaranteeExceeded) mustBe JsNumber(8)
      val accountCancelled: CDSAccountStatusId = AccountCancelled
      Json.toJson(accountCancelled) mustBe JsNumber(9)
    }
  }
}
