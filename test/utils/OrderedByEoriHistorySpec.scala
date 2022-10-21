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

import util.SpecBase
import viewmodels.DutyDefermentStatementsForEori

class OrderedByEoriHistorySpec extends SpecBase {

  "OrderedByEoriHistory" should {

    "compare returns no valid result" in {
      lazy val testData: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
        eoriHistory, dutyDefermentStatementFiles, dutyDefermentStatementFiles
      )
      val result = dutyDefermentStatementsForEori.compare(testData)
      result mustBe 1
    }

    "compare returns no yield GetOrElse(1)" in {
      val result = dutyDefermentStatementsForEori.compare(dutyDefermentStatementsForEori)
      result mustBe 1
    }
  }
}
