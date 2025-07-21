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

import models.AccountDetails
import util.SpecBase
import play.api.libs.json.{JsResultException, JsSuccess, Json}

class ResponseDetailSpec extends SpecBase {

  "format" should {
    "generate correct output for Json Reads" in new Setup {
      import ResponseDetail.format

      Json.fromJson(Json.parse(responseDetailJsString)) mustBe JsSuccess(responseDetailOb)
    }

    "generate correct output for Json Writes" in new Setup {
      Json.toJson(responseDetailOb) mustBe Json.parse(responseDetailJsString)
    }

    "throw exception for invalid Json" in {
      val invalidJson = "{ \"eori1\": \"pending\", \"accountDetails\": \"test_event\" }"

      intercept[JsResultException] {
        Json.parse(invalidJson).as[ResponseDetail]
      }
    }
  }

  trait Setup {
    val accDetails: AccountDetails     = AccountDetails(accountType, validDan)
    val contactDetails: ContactDetails = ContactDetails(
      contactName = Some("John Smith"),
      addressLine1 = "1 High Street",
      addressLine2 = Some("Town"),
      addressLine3 = Some("The County"),
      addressLine4 = Some("England"),
      postCode = Some("AB12 3CD"),
      countryCode = "0044",
      telephone = Some("1234567"),
      faxNumber = Some("7654321"),
      email = Some("abc@de.com")
    )

    val responseDetailOb: ResponseDetail =
      ResponseDetail(eori = validEori, accountDetails = accDetails, contactDetails = contactDetails)

    val responseDetailJsString: String =
      """{"eori":"someEori",
        |"accountDetails":{"accountType":"CDS Cash Account","accountNumber":"someDan"},
        |"contactDetails":{"contactName":"John Smith","addressLine1":"1 High Street","addressLine2":"Town",
        |"addressLine3":"The County",
        |"addressLine4":"England",
        |"postCode":"AB12 3CD",
        |"countryCode":"0044",
        |"telephone":"1234567",
        |"faxNumber":"7654321",
        |"email":"abc@de.com"
        |}}""".stripMargin
  }
}
