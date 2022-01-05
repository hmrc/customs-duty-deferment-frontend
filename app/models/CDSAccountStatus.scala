/*
 * Copyright 2022 HM Revenue & Customs
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

sealed trait CDSAccountStatus {
  val name: String
}
case object AccountStatusOpen extends CDSAccountStatus { override val name: String = "open" }
case object AccountStatusClosed extends CDSAccountStatus { override val name: String = "closed" }
case object AccountStatusSuspended extends CDSAccountStatus { override val name: String = "suspended" }
case object AccountStatusPending extends CDSAccountStatus { override val name: String = "pending" }

object CDSAccountStatus {
  import play.api.Logger
  val logger: Logger = Logger(this.getClass)

  implicit val CDSAccountStatusReads: Format[CDSAccountStatus] = new Format[CDSAccountStatus] {
    override def writes(accountStatus: CDSAccountStatus): JsValue = JsString(accountStatus.name)

    override def reads(json: JsValue): JsResult[CDSAccountStatus] = {
      json.as[String].toUpperCase match {
        case "OPEN" => JsSuccess(AccountStatusOpen)
        case "SUSPENDED" => JsSuccess(AccountStatusSuspended)
        case "CLOSED" => JsSuccess(AccountStatusClosed)
        case "PENDING" => JsSuccess(AccountStatusPending)
        case e => logger.warn(s"Invalid account status: $e"); JsSuccess(AccountStatusOpen)
      }
    }
  }
}
