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

import config.AppConfig
import models.responses.retrieve.ContactDetails
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.cache.repository.CacheMongoRepository
import javax.inject.Inject

import scala.concurrent.ExecutionContext

class ContactDetailsCache @Inject()(appConfig: AppConfig, mongo: MongoComponent)
                                   (override implicit val ec: ExecutionContext) extends
  CacheMongoRepository("contact-details-cache", appConfig.mongoSessionContactDetailsTtl)(mongo.mongoConnector.db, ec) with
  SessionCache[ContactDetails] {

    override val key: String = "contactDetails"

}


