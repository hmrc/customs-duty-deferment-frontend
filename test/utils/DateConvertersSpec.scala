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

import java.time.LocalDate
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
      result mustBe 1000002022
    }

    "OrderedLocalDate fails if dates are not equal" in {
      val date = LocalDate.now()
      val result = OrderedLocalDate(date).compare(LocalDate.now())
      result mustBe 0
    }

    "DutyDeferementPeriodStatements can have their Date Converter Compared successfully" in {

      val currDate = LocalDate.now();
      val prior3Days = currDate.minusDays(3)
      val prior1Day = currDate.minusDays(1)

      val deferement1 = DutyDefermentStatementPeriod(
        FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
        currDate, currDate, currDate, Seq.empty)

      deferement1.compare(
        DutyDefermentStatementPeriod(
          FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
          currDate, currDate, currDate, Seq.empty)) mustBe 0;

      deferement1.compare(
        DutyDefermentStatementPeriod(
          FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
          currDate, prior3Days, prior1Day, Seq.empty)) mustBe -1;

      deferement1.compare(
        DutyDefermentStatementPeriod(
          FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
          currDate, prior1Day, currDate, Seq.empty)) mustBe 1;
    }
  }
}

