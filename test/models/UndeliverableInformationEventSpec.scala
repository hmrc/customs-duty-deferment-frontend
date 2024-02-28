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

import play.api.libs.json.{JsSuccess, Json}
import util.SpecBase

class UndeliverableInformationEventSpec extends SpecBase {

  "Writes" should {
    "generate the correct JsValue" in new Setup {
      Json.toJson(undelInfoEventOb) mustBe Json.parse(sampleResponse)
    }
  }

  "Reads" should {
    "generate the correct object" in new Setup {

      import UndeliverableInformationEvent.format

      Json.fromJson(Json.parse(sampleResponse)) mustBe JsSuccess(undelInfoEventOb)
    }
  }

  trait Setup {
    val sampleResponse: String =
      """{
        |  "id": "example-id",
        |  "event": "someEvent",
        |  "emailAddress": "email@email.com",
        |  "detected": "2021-05-14T10:59:45.811+01:00",
        |  "code": 12,
        |  "reason": "Inbox full",
        |  "enrolment": "HMRC-CUS-ORG~EORINumber~GB744638982004"
        |}""".stripMargin

    val eventCode = 12
    val undelInfoEventOb: UndeliverableInformationEvent = UndeliverableInformationEvent("example-id",
      "someEvent",
      "email@email.com",
      "2021-05-14T10:59:45.811+01:00",
      Some(eventCode),
      Some("Inbox full"),
      "HMRC-CUS-ORG~EORINumber~GB744638982004")
  }
}
