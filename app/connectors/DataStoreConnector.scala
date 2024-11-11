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
import models._
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.auth.core.retrieve.Email
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataStoreConnector @Inject()(httpClient: HttpClientV2,
                                   appConfig: AppConfig)
                                  (implicit executionContext: ExecutionContext) {

  def getAllEoriHistory(eori: String)(implicit hc: HeaderCarrier): Future[Seq[EoriHistory]] =
    httpClient.get(url"${appConfig.customsDataStore}/eori/$eori/eori-history")
      .execute[EoriHistoryResponse]
      .flatMap {
        response => Future.successful(response.eoriHistory)
      }
      .recover { case _ => Seq(EoriHistory(eori, None, None)) }

  def getEmail(eori: String)(implicit hc: HeaderCarrier): Future[Either[EmailResponses, Email]] = {
    val dataStoreEndpoint = s"${appConfig.customsDataStore}/eori/$eori/verified-email"

    httpClient.get(url"$dataStoreEndpoint")
      .execute[EmailResponse]
      .flatMap {
        case EmailResponse(Some(address), _, None) => Future.successful(Right(Email(address)))
        case EmailResponse(Some(email), _, Some(_)) => Future.successful(Left(UndeliverableEmail(email)))
        case _ => Future.successful(Left(UnverifiedEmail))
      }.recover {
      case UpstreamErrorResponse(_, NOT_FOUND, _, _) => Left(UnverifiedEmail)
    }
  }

  def verifiedEmail(implicit hc: HeaderCarrier): Future[EmailVerifiedResponse] = {
    val emailDisplayApiUrl = s"${appConfig.customsDataStore}/subscriptions/email-display"

    httpClient
      .get(url"$emailDisplayApiUrl")
      .execute[EmailVerifiedResponse]
      .flatMap {
        response => Future.successful(response)
      }
  }

  def retrieveUnverifiedEmail(implicit hc: HeaderCarrier): Future[Option[EmailUnverifiedResponse]] = {
    val unverifiedEmailDisplayApiUrl = s"${appConfig.customsDataStore}/subscriptions/unverified-email-display"

    httpClient
      .get(url"$unverifiedEmailDisplayApiUrl")
      .execute[EmailUnverifiedResponse]
      .map(response => Some(response)) 
      .recover { case _ => None }
  }
}
