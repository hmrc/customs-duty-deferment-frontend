/*
 * Copyright 2021 HM Revenue & Customs
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
import models.UserAnswers
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.cache.repository.CacheMongoRepository

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UserAnswersCache @Inject()(appConfig: AppConfig, mongo: ReactiveMongoComponent)
                                (override implicit val ec: ExecutionContext) extends
  CacheMongoRepository("user-answers", appConfig.mongoSessionTtl)(mongo.mongoConnector.db, ec) with
  SessionCache[UserAnswers] {

  override val key: String = "userAnswers"

}

