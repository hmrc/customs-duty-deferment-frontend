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

package services

import cache.AccountLinkCache
import connectors.SessionCacheConnector
import models.{AccountLink, DutyDefermentAccountLink}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class AccountLinkCacheService @Inject()(
                                         sessionCacheConnector: SessionCacheConnector,
                                         accountLinkCache: AccountLinkCache
                                       )(implicit executionContext: ExecutionContext) {
  def cacheAccountLink(linkId: String, sessionId: String, internalId: String)(implicit hc: HeaderCarrier)
  : Future[Either[SessionCacheError, DutyDefermentAccountLink]] = {
    sessionCacheConnector.retrieveSession(sessionId, linkId).flatMap {
      case None => Future.successful(Left(NoDutyDefermentSessionAvailable))
      case Some(AccountLink(_, _, _, _, None, _)) => Future.successful(Left(NoDutyDefermentSessionAvailable))
      case Some(AccountLink(eori, accountNumber, _, accountStatus, Some(accountStatusId), isNiAccount)) =>
        val details = DutyDefermentAccountLink(eori, accountNumber, linkId, accountStatus, accountStatusId, isNiAccount)
        accountLinkCache.store(internalId, details).map(_ => Right(details))
    }
  }

  def get(internalId: String): Future[Option[DutyDefermentAccountLink]] = {
    accountLinkCache.retrieve(internalId)
  }

  def remove(internalId: String): Future[Boolean] = {
    accountLinkCache.remove(internalId)
  }
}

sealed trait SessionCacheError

case object NoAccountStatusId extends SessionCacheError

case object NoDutyDefermentSessionAvailable extends SessionCacheError
