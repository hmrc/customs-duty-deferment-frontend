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

package connectors

import config.AppConfig
import models.{SDDSRequest, SDDSResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SDDSConnector @Inject()(httpClient: HttpClient,
                              appConfig: AppConfig
                             )(implicit executionContext: ExecutionContext) {

  lazy val returnUrl: String = appConfig.financialsHomepage
  lazy val backUrl: String = appConfig.financialsHomepage

  def startJourney(dan: String, email: String)(implicit hc: HeaderCarrier): Future[String] = {
    httpClient.POST[SDDSRequest, SDDSResponse](
      appConfig.sddsUri,
      SDDSRequest(returnUrl, backUrl, dan, email)
    ).map(_.nextUrl)
  }
}
