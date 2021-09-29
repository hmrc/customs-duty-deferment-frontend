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

package services

import cache.DutyDefermentAccountCache
import connectors.SessionCacheConnector
import models.{AccountLink, DutyDefermentDetails}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DutyDefermentCacheService @Inject()(
                                           sessionCacheConnector: SessionCacheConnector,
                                           dutyDefermentAccountCache: DutyDefermentAccountCache
                                         )(implicit executionContext: ExecutionContext) {

  sealed trait SessionCacheError

  case object NoAccountStatusId extends SessionCacheError

  case object NoDutyDefermentSessionAvailable extends SessionCacheError

  def getAndCache(linkId: String, sessionId: String, internalId: String)(implicit hc: HeaderCarrier): Future[Either[SessionCacheError, DutyDefermentDetails]] = {
    sessionCacheConnector.retrieveSession(sessionId, linkId).flatMap {
      case None => Future.successful(Left(NoDutyDefermentSessionAvailable))
      case Some(AccountLink(_, _, _, None)) => Future.successful(Left(NoDutyDefermentSessionAvailable))
      case Some(AccountLink(accountNumber, _, accountStatus, Some(accountStatusId))) =>
        val details = DutyDefermentDetails(accountNumber, linkId, accountStatus, accountStatusId)
        dutyDefermentAccountCache.store(internalId, details).map(_ => Right(details))
    }
  }

  def get(internalId: String): Future[Option[DutyDefermentDetails]] = {
    dutyDefermentAccountCache.retrieve(internalId)
  }

  def remove(internalId: String): Future[Boolean] = {
    dutyDefermentAccountCache.remove(internalId)
  }
}
