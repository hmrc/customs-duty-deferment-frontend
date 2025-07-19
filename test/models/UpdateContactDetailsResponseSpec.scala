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

class UpdateContactDetailsResponseSpec extends SpecBase {

  "reads" should {

    "Read the object correctly" in {
      import UpdateContactDetailsResponse.reads

      val updateContactDetailsResponseWithSuccess: UpdateContactDetailsResponse = UpdateContactDetailsResponse(true)
      val updateContactDetailsResponseWithFailure: UpdateContactDetailsResponse = UpdateContactDetailsResponse(false)

      val updateContactDetailsResponseWithSuccessJsString: String = """{"success":true}""".stripMargin
      val updateContactDetailsResponseWithFailureJsString: String = """{"success":false}""".stripMargin

      Json.fromJson(Json.parse(updateContactDetailsResponseWithSuccessJsString)) mustBe JsSuccess(
        updateContactDetailsResponseWithSuccess
      )

      Json.fromJson(Json.parse(updateContactDetailsResponseWithFailureJsString)) mustBe JsSuccess(
        updateContactDetailsResponseWithFailure
      )
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"key1\": \"test_key\", \"eventId1\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[UpdateContactDetailsResponse]
      }
    }
  }
}
