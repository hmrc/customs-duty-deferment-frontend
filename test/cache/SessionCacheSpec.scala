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
 * Copyright 2021 HM Revenue & Customs
 *
 */

package cache

import play.api.test.Helpers._
import util.SpecBase

class SessionCacheSpec extends SpecBase {
  trait Setup {
    protected val id = "session-123"
    protected val test: Test = Test("john doe", 123)
  }

  "Session Cache" must {
    ".set a value successfully" in new Setup {
      private val app = application().build()
      running(app) {
        val testCache = app.injector.instanceOf[TestCache]
        val storeResult = await(testCache.store(id, test))
        storeResult mustBe true
      }
    }

    ".set and then .get the value successfully" in new Setup {
      private val app = application().build()
      running(app) {
        val testCache = app.injector.instanceOf[TestCache]
        await(testCache.store(id, test))
        val retrieved = await(testCache.retrieve(id))
        retrieved.get mustBe test
      }
    }
  }
}