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

package utils

import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import utils.Utils._
import org.scalatest.matchers.should.Matchers.shouldBe

import java.time.LocalDate

class UtilsSpec extends SpecBase {
  "emptyString" must {
    "contain the empty string" in {
      emptyString mustBe empty
    }
  }

  "hyphen" must {
    "return correct value" in {
      hyphen mustBe "-"
    }
  }

  "pathWithQueryString" must {
    "return correct value" in {
      val path = "somePath"
      pathWithQueryString(fakeRequest("GET", path)) mustBe s"$path"
    }
  }

  "referrerUrl" must {
    "return correct value when platform host has some value" in {
      val path                                                     = "somePath"
      val platformHost                                             = "localhost"
      implicit val reqHeaders: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", path)

      referrerUrl(Some(platformHost)) mustBe Option(s"$platformHost$path")
    }

    "return correct value when platform host value is empty" in {
      val path                                                     = "somePath"
      implicit val reqHeaders: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", path)

      referrerUrl(None) mustBe Option(s"$path")
    }
  }

  "isEqualOrAfter" must {
    "return true when date is equal to or after cutOffDate" in new Setup {
      isEqualOrAfter(date03, cutOffDate) shouldBe true
      isEqualOrAfter(date04, cutOffDate) shouldBe true
    }

    "return false when date is before cutOffDate" in new Setup {
      isEqualOrAfter(date02, cutOffDate) shouldBe false
    }
  }

  "isEqualOrBefore" must {
    "isEqualOrBefore should return true when date is equal to or before cutOffDate" in new Setup {
      isEqualOrBefore(date03, cutOffDate) shouldBe true
      isEqualOrBefore(date02, cutOffDate) shouldBe true
    }

    "isEqualOrBefore should return false when date is after cutOffDate" in new Setup {
      isEqualOrBefore(date04, cutOffDate) shouldBe false
    }
  }

  "firstDayOfPastNthMonth" must {
    "return the first day of the month 3 months ago" in new Setup {
      firstDayOfPastNthMonth(date03, numberOfMonths) shouldBe date01
    }
  }

  trait Setup {
    val year_2024: Int = 2024
    val month_06: Int  = 6
    val month_03: Int  = 3

    val day_01: Int = 1
    val day_02: Int = 2
    val day_03: Int = 3
    val day_04: Int = 4

    val cutOffDate = LocalDate.of(year_2024, month_06, day_03)
    val date01     = LocalDate.of(year_2024, month_03, day_01)
    val date02     = LocalDate.of(year_2024, month_06, day_02)
    val date03     = LocalDate.of(year_2024, month_06, day_03)
    val date04     = LocalDate.of(year_2024, month_06, day_04)

    val numberOfMonths = 3
  }

}
