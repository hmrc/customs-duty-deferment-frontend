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

package connectors

import config.AppConfig
import models.{EoriHistory, EoriHistoryResponse}
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataStoreConnector @Inject()(http: HttpClient,
                                   appConfig: AppConfig)
                                  (implicit executionContext: ExecutionContext) extends Logging {
  def getAllEoriHistory(eori: String)(implicit hc: HeaderCarrier): Future[Seq[EoriHistory]] = {
    http.GET[EoriHistoryResponse](appConfig.customsDataStore + s"/eori/$eori/eori-history")
      .map(response => response.eoriHistory)
      .recover { case _ => Seq(EoriHistory(eori, None, None)) }
  }
}
