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
import models.AccountLink
import play.api.http.Status.OK
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SessionCacheConnector @Inject()(httpClient: HttpClientV2,
                                      appConfig: AppConfig)(implicit executionContext: ExecutionContext) {

  def retrieveSession(id: String, linkId: String)(implicit hc: HeaderCarrier): Future[Option[AccountLink]] = {

    val endpointUrl = appConfig.customsSessionCacheUrl + s"/account-link/$id/$linkId"

    httpClient.get(url"$endpointUrl")
      .execute[AccountLink]
      .flatMap {
        response => Future.successful(Some(response))
      }.recover{
      case _ => None
    }
  }

  /*httpClient.GET[AccountLink](
      appConfig.customsSessionCacheUrl + s"/account-link/$id/$linkId"
    ).map(Some(_)).recover { case _ => None }*/

  def removeSession(id: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val endpointUrl = s"${appConfig.customsSessionCacheUrl}/remove/$id"

    httpClient.delete(url"$endpointUrl")
      .execute[HttpResponse]
      .flatMap {
        response => Future.successful(response.status == OK)
      }.recover {
      case _ => false
    }

   /* httpClient.DELETE[HttpResponse](
      appConfig.customsSessionCacheUrl + "/remove/" + id
    ).map(_.status == OK).recover { case _ => false }*/
  }
}
