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

import play.api.libs.json._

sealed trait CDSAccountStatusId {
  val value: Int
}

case object DefermentAccountAvailable extends CDSAccountStatusId {
  val value: Int = 0
}

case object ChangeOfLegalEntity extends CDSAccountStatusId {
  val value: Int = 1
}

case object GuaranteeCancelledGuarantorsRequest extends CDSAccountStatusId {
  val value: Int = 2
}

case object GuaranteeCancelledTradersRequest extends CDSAccountStatusId {
  val value: Int = 3
}

case object DirectDebitMandateCancelled extends CDSAccountStatusId {
  val value: Int = 4
}

case object DebitRejectedAccountClosedOrTransferred extends CDSAccountStatusId {
  val value: Int = 5
}

case object DebitRejectedReferToDrawer extends CDSAccountStatusId {
  val value: Int = 6
}

case object ReturnedMailOther extends CDSAccountStatusId {
  val value: Int = 7
}

case object GuaranteeExceeded extends CDSAccountStatusId {
  val value: Int = 8
}

case object AccountCancelled extends CDSAccountStatusId {
  val value: Int = 9
}

object CDSAccountStatusId {

  import play.api.Logger

  val logger: Logger = Logger(this.getClass)

  private val values: Set[CDSAccountStatusId] = Set(
    DefermentAccountAvailable,
    ChangeOfLegalEntity,
    GuaranteeCancelledGuarantorsRequest,
    GuaranteeCancelledTradersRequest,
    DirectDebitMandateCancelled,
    DebitRejectedAccountClosedOrTransferred,
    DebitRejectedReferToDrawer,
    ReturnedMailOther,
    GuaranteeExceeded,
    AccountCancelled
  )

  implicit val CDSAccountStatusIdReads: Format[CDSAccountStatusId] = new Format[CDSAccountStatusId] {
    override def writes(accountStatusId: CDSAccountStatusId): JsValue = JsNumber(accountStatusId.value)

    override def reads(json: JsValue): JsResult[CDSAccountStatusId] =
      JsSuccess(
        json.asOpt[Int] match {
          case Some(statusId) =>
            values.find(_.value == statusId).getOrElse {
              logger.warn(s"Invalid account status id: $statusId")
              DefermentAccountAvailable
            }
          case None           =>
            logger.warn(s"No account status id in JSON")
            DefermentAccountAvailable
        }
      )

  }
}
