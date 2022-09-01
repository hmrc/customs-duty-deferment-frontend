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
import javax.inject.Inject
import uk.gov.hmrc.mongo.play.PlayMongoComponent
import scala.concurrent.ExecutionContext

class AccountLinkCache @Inject()(appConfig: AppConfig, mongo: PlayMongoComponent)
                                (override implicit val ec: ExecutionContext) extends
  CacheMongoRepository("account-link-cache", appConfig.mongoAccountLinkTtl)(mongo.mongoConnector.db, ec) with
  SessionCache[DutyDefermentAccountLink] {

  override val key: String = "dutyDefermentAccountLink"
}
