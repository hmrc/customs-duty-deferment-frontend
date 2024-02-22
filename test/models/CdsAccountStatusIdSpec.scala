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

import play.api.libs.json.{JsNumber, JsString, Json}
import util.SpecBase

class CdsAccountStatusIdSpec extends SpecBase {

  "CdsAccountStatusId" should {
    "read/write the correct values" in {

      val val0 = 0
      val val1 = 1
      val val2 = 2
      val val3 = 3
      val val4 = 4
      val val5 = 5
      val val6 = 6
      val val7 = 7
      val val8 = 8
      val val9 = 9
      val val99 = 99

      JsNumber(val0).as[CDSAccountStatusId] mustBe DefermentAccountAvailable
      JsNumber(val1).as[CDSAccountStatusId] mustBe ChangeOfLegalEntity
      JsNumber(val2).as[CDSAccountStatusId] mustBe GuaranteeCancelledGuarantorsRequest
      JsNumber(val3).as[CDSAccountStatusId] mustBe GuaranteeCancelledTradersRequest
      JsNumber(val4).as[CDSAccountStatusId] mustBe DirectDebitMandateCancelled
      JsNumber(val5).as[CDSAccountStatusId] mustBe DebitRejectedAccountClosedOrTransferred
      JsNumber(val6).as[CDSAccountStatusId] mustBe DebitRejectedReferToDrawer
      JsNumber(val7).as[CDSAccountStatusId] mustBe ReturnedMailOther
      JsNumber(val8).as[CDSAccountStatusId] mustBe GuaranteeExceeded
      JsNumber(val9).as[CDSAccountStatusId] mustBe AccountCancelled
      JsNumber(val99).as[CDSAccountStatusId] mustBe DefermentAccountAvailable
      JsString("").as[CDSAccountStatusId] mustBe DefermentAccountAvailable

      val defermentAccountAvailable: CDSAccountStatusId = DefermentAccountAvailable
      Json.toJson(defermentAccountAvailable) mustBe JsNumber(val0)
      val changeOfLegalEntity: CDSAccountStatusId = ChangeOfLegalEntity
      Json.toJson(changeOfLegalEntity) mustBe JsNumber(val1)
      val guaranteeCancelledGuarantorsRequest: CDSAccountStatusId = GuaranteeCancelledGuarantorsRequest
      Json.toJson(guaranteeCancelledGuarantorsRequest) mustBe JsNumber(val2)
      val guaranteeCancelledTradersRequest: CDSAccountStatusId = GuaranteeCancelledTradersRequest
      Json.toJson(guaranteeCancelledTradersRequest) mustBe JsNumber(val3)
      val directDebitMandateCancelled: CDSAccountStatusId = DirectDebitMandateCancelled
      Json.toJson(directDebitMandateCancelled) mustBe JsNumber(val4)
      val debitRejectedAccountClosedOrTransferred: CDSAccountStatusId = DebitRejectedAccountClosedOrTransferred
      Json.toJson(debitRejectedAccountClosedOrTransferred) mustBe JsNumber(val5)
      val debitRejectedReferToDrawer: CDSAccountStatusId = DebitRejectedReferToDrawer
      Json.toJson(debitRejectedReferToDrawer) mustBe JsNumber(val6)
      val returnedMailOther: CDSAccountStatusId = ReturnedMailOther
      Json.toJson(returnedMailOther) mustBe JsNumber(val7)
      val guaranteeExceeded: CDSAccountStatusId = GuaranteeExceeded
      Json.toJson(guaranteeExceeded) mustBe JsNumber(val8)
      val accountCancelled: CDSAccountStatusId = AccountCancelled
      Json.toJson(accountCancelled) mustBe JsNumber(val9)
    }
  }
}
