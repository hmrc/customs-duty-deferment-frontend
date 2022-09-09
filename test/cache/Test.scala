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

import cache.TestCache.ttl
import play.api.Configuration
import play.api.libs.json.{Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.cache.repository.CacheMongoRepository
import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}
import uk.gov.hmrc.mongo.cache.{CacheIdType, MongoCacheRepository}

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

case class Test(name: String, id: Int)

object Test {
  implicit val formats: OFormat[Test] = Json.format[Test]
}

class TestCache @Inject()(mongo: ReactiveMongoComponent)
                         (override implicit val ec: ExecutionContext) extends
  CacheMongoRepository("test", ttl)(mongo.mongoConnector.db, ec) with
  SessionCache[Test] {
  override val key: String = "test"
}

class HmrcMongoTestCache @Inject()(
                                    mongoComponent: MongoComponent,
                                    configuration: Configuration,
                                    timestampSupport: TimestampSupport
                                  )(override implicit val ec: ExecutionContext) extends MongoCacheRepository[String](
  mongoComponent = mongoComponent,
  collectionName = "test",
  ttl = 900.seconds,
  timestampSupport = timestampSupport,
  cacheIdType = CacheIdType.SimpleCacheId
) with HmrcMongoSessionCache[Test] {
  override val key: String = "test"
}

object TestCache {
  protected val ttl = 900
}
