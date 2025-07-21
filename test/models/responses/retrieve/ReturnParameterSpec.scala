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

package models.responses.retrieve

import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class ReturnParameterSpec extends SpecBase {

  "format" should {
    "generate correct output for Json Reads" in new Setup {
      import ReturnParameter.format

      Json.fromJson(Json.parse(returnParamJsString)) mustBe JsSuccess(returnParamOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(returnParamOb) mustBe Json.parse(returnParamJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"status\": \"pending\", \"eventId1\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[ReturnParameter]
      }
    }
  }

  trait Setup {
    val returnParamOb: ReturnParameter = ReturnParameter("test_param", "test_param_value")
    val returnParamJsString: String    = """{"paramName":"test_param","paramValue":"test_param_value"}""".stripMargin
  }
}
