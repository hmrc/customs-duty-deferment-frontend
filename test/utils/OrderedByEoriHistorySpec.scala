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
package utils

import models.{DDStatementType, FileRole}
import util.SpecBase
import viewmodels.{DutyDefermentStatementPeriod, DutyDefermentStatementsForEori}

import java.time.LocalDate

class OrderedByEoriHistorySpec extends SpecBase {

  "OrderedByEoriHistory" should {

    "compare returns no valid result" in {
      lazy val testData: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
        eoriHistory,
        dutyDefermentStatementFiles,
        dutyDefermentStatementFiles,
        LocalDate.now()
      )

      val result = dutyDefermentStatementsForEori01.compare(testData)

      result mustBe 1
    }

    "compare returns no yield GetOrElse(1)" in {
      val result = dutyDefermentStatementsForEori01.compare(dutyDefermentStatementsForEori01)
      result mustBe 1
    }

    "sort the data in correct order" in {
      val ddStatForEori1: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
        eoriHistory.copy(validFrom = Some(LocalDate.of(year2023, month_12, day_01))),
        dutyDefermentStatementFiles,
        dutyDefermentStatementFiles,
        LocalDate.of(year2023, month_12, day_01)
      )

      val ddStatForEori2: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
        eoriHistory.copy(validFrom = Some(LocalDate.of(year2023, month_12, day10))),
        dutyDefermentStatementFiles,
        dutyDefermentStatementFiles,
        LocalDate.of(year2023, month_12, day10)
      )

      List(ddStatForEori1, ddStatForEori2).sorted mustBe List(ddStatForEori2, ddStatForEori1)
    }

    "compare DutyDefermentPeriod with another period" in {
      val year         = 2023
      val month        = 1
      val dayOfMonth01 = 1
      val dayOfMonth02 = 2
      val dayOfMonth30 = 30
      val dayOfMonth31 = 31

      val startDate01 = LocalDate.of(year, month, dayOfMonth01)
      val startDate02 = LocalDate.of(year, month, dayOfMonth02)
      val endDate01   = LocalDate.of(year, month, dayOfMonth30)
      val endDate02   = LocalDate.of(year, month, dayOfMonth31)

      val period1 = DutyDefermentStatementPeriod(
        FileRole.DutyDefermentStatement,
        DDStatementType.Supplementary,
        startDate01,
        startDate01,
        endDate01
      )
      val period2 = DutyDefermentStatementPeriod(
        FileRole.DutyDefermentStatement,
        DDStatementType.Supplementary,
        startDate02,
        startDate02,
        endDate02
      )
      val period3 = DutyDefermentStatementPeriod(
        FileRole.DutyDefermentStatement,
        DDStatementType.Excise,
        startDate01,
        startDate01,
        endDate02
      )

      period1.compare(period1) mustBe 0
      period1.compare(period2) mustBe 1
      period1.compare(period3) mustBe 1
    }
  }
}
