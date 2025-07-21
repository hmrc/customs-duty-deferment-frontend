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

class CountrySpec extends SpecBase {

  "format" should {
    "generate correct output for Json Reads" in new Setup {
      import Country.formats

      Json.fromJson(Json.parse(countryJsString)) mustBe JsSuccess(countryOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(countryOb) mustBe Json.parse(countryJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"countryName\": \"GERMANY\", \"countryCode12\": \"GER\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[Country]
      }
    }
  }

  trait Setup {
    val countryOb: Country      = Country(countryName, countryCode)
    val countryJsString: String = """{"countryName":"UNITED KINGDOM","countryCode":"GB"}""".stripMargin
  }
}
