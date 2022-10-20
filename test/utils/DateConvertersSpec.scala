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

import java.time.{LocalDate}
import models.{DDStatementType, FileRole}
import util.SpecBase
import utils.DateConverters.OrderedLocalDate
import viewmodels.DutyDefermentStatementPeriod

class DateConvertersSpec extends SpecBase {

  "DateConverters" should {
    "OrderedLocalDate can be used to compare dates" in {
      val date = LocalDate.now()
      val result = OrderedLocalDate(date).compare(date)
      result mustBe 0
    }

    "OrderedLocalDate fails if dates are not valid" in {
      val diffTime = LocalDate.MIN
      val result = OrderedLocalDate(LocalDate.now()).compare(diffTime)
      result mustBe 1000002021
    }

    "OrderedLocalDate fails if dates are not equal" in {
      val date = LocalDate.now()
      val result = OrderedLocalDate(date).compare(LocalDate.now())
      result mustBe 0
    }

    "DutyDeferementPeriodStatements can have their Date Converter Compared successfully" in {

      val deferement1 = DutyDefermentStatementPeriod(
        FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
        LocalDate.now(), LocalDate.now(), LocalDate.now(), Seq.empty)

      val deferement2 = DutyDefermentStatementPeriod(
        FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
        LocalDate.now(), LocalDate.now(), LocalDate.now(), Seq.empty)

      val testData: Seq[DutyDefermentStatementPeriod] = Seq(deferement2, deferement1)

      val monthYearSorted = testData.groupBy(_.monthAndYear).toSeq.sortWith(_._1 > _._1)
    }
  }
}
