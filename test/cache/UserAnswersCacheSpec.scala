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

package cache

import models.UserAnswers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import util.SpecBase

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class UserAnswersCacheSpec extends SpecBase with BeforeAndAfterEach with OptionValues with ScalaFutures with IntegrationPatience {

  private val id = "session-123"
  private val test: UserAnswers = UserAnswers("id", lastUpdated = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
  private val app = application().build()
  private val testCache = app.injector.instanceOf[UserAnswersCache]

  override def beforeEach(): Unit =
    testCache.collection.drop().toFuture().futureValue

  "Session Cache" must {

    ".set a value successfully" in {
      val storeResult = testCache.store(id, test).futureValue
      storeResult mustBe true
    }

    ".set and then .get the value successfully" in {
      testCache.store(id, test).futureValue
      val retrieved = testCache.retrieve(id).futureValue
      retrieved.value mustBe test
    }

    ".get call after data removed from cache returns none" in {
      val storeResult = testCache.store(id, test).futureValue
      storeResult mustBe true
      val removeResult = testCache.remove(id).futureValue
      removeResult mustBe true
      val retrieveResult = testCache.retrieve(id).futureValue
      retrieveResult mustBe None
    }

    ".set and then remove value successfully" in {
      val storeResult = testCache.store(id, test).futureValue
      storeResult mustBe true
      val removeResult = testCache.remove(id).futureValue
      removeResult mustBe true
    }
  }
}