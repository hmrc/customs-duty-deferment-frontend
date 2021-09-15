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
import models.{AccountLink, SessionCacheAccountLink}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class AccountLinksRequest(sessionId: String,
                               accountLinks: Seq[SessionCacheAccountLink])

object AccountLinksRequest {
  implicit val format: OFormat[AccountLinksRequest] = Json.format[AccountLinksRequest]
}


class SessionCacheConnector @Inject()(httpClient: HttpClient,
                                                       appConfig: AppConfig)(implicit executionContext: ExecutionContext) {

  def retrieveSession(id: String, linkId: String)(implicit hc: HeaderCarrier): Future[Option[AccountLink]] = {
    val sessionCacheUrl = appConfig.customsFinancialsSessionCacheUrl + s"/account-link/$id/$linkId"
    httpClient.GET[SessionCacheAccountLink](sessionCacheUrl).map(response => Some(new AccountLink(id, response)))
      .recover {
        case _ => None
      }
  }

  def storeSession(id: String, accountLinks: Seq[AccountLink])(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val sessionCacheUrl = appConfig.customsFinancialsSessionCacheUrl + "/update-links"
    val request: AccountLinksRequest = AccountLinksRequest(id, toSessionCacheAccountLinks(accountLinks))
    httpClient.POST[AccountLinksRequest, HttpResponse](sessionCacheUrl, request)
  }

  def removeSession(id: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val sessionCacheUrl = appConfig.customsFinancialsSessionCacheUrl + "/remove/" + id
    httpClient.DELETE[HttpResponse](sessionCacheUrl)
  }

  private def toSessionCacheAccountLinks(accountLinks: Seq[AccountLink]): Seq[SessionCacheAccountLink] = for {
    accountLink <- accountLinks
    sessionAccountLink = SessionCacheAccountLink(accountLink.eori, accountLink.accountNumber, accountLink.accountStatus, accountLink.accountStatusId, accountLink.linkId)
  } yield sessionAccountLink

}
