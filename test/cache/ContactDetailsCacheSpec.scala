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

package cache

import models.responses.retrieve.ContactDetails
import org.mongodb.scala.SingleObservableFuture
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import util.SpecBase

class ContactDetailsCacheSpec
    extends SpecBase
    with BeforeAndAfterEach
    with OptionValues
    with ScalaFutures
    with IntegrationPatience {

  private val id        = "session-123"
  private val testCache = application().injector.instanceOf[ContactDetailsCache]

  override def beforeEach(): Unit =
    testCache.collection.drop().toFuture().futureValue

  private val test: ContactDetails = ContactDetails(
    contactName = Some("contactName"),
    addressLine1 = "addressLine1",
    addressLine2 = Some("addressLine2"),
    addressLine3 = Some("addressLine3"),
    addressLine4 = Some("addressLine4"),
    postCode = Some("postcode"),
    countryCode = "countryCode",
    telephone = Some("telephone"),
    faxNumber = Some("faxNumber"),
    email = Some("email")
  )

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

    ".get call when data not found from cache returns none" in {
      testCache.store(id + 1, test).futureValue
      val retrieved = testCache.retrieve(id).futureValue
      retrieved mustBe None
    }

    ".get call after data removed from cache returns none" in {
      val storeResult    = testCache.store(id, test).futureValue
      storeResult mustBe true
      val removeResult   = testCache.remove(id).futureValue
      removeResult mustBe true
      val retrieveResult = testCache.retrieve(id).futureValue
      retrieveResult mustBe None
    }

    ".set and then remove value successfully" in {
      val storeResult  = testCache.store(id, test).futureValue
      storeResult mustBe true
      val removeResult = testCache.remove(id).futureValue
      removeResult mustBe true
    }
  }
}
