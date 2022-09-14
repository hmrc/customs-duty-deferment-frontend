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
import models.DutyDefermentAccountLink
import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}
import uk.gov.hmrc.mongo.cache.{CacheIdType, MongoCacheRepository}

import javax.inject.{Singleton, Inject}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class AccountLinkCache @Inject()(
                                  appConfig: AppConfig,
                                  mongoComponent: MongoComponent,
                                  timestampSupport: TimestampSupport
                                )
                                (override implicit val ec: ExecutionContext) extends
  MongoCacheRepository(
    mongoComponent = mongoComponent,
    collectionName = "account-link-cache",
    ttl = appConfig.mongoAccountLinkTtl.seconds,
    timestampSupport = timestampSupport,
    cacheIdType = CacheIdType.SimpleCacheId
  ) with SessionCache[DutyDefermentAccountLink] {

  override val key: String = "dutyDefermentAccountLink"
}
