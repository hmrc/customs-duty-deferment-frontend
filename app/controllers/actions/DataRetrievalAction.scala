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

package controllers.actions

import cache.UserAnswersCache
import models.requests.OptionalDataRequest
import play.api.mvc.ActionTransformer

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject() (userAnswersCache: UserAnswersCache)(implicit
  val executionContext: ExecutionContext
) extends DataRetrievalAction {

  override protected def transform[A](request: AuthenticatedRequestWithSessionId[A]): Future[OptionalDataRequest[A]] =
    userAnswersCache.retrieve(request.request.user.internalId).map {
      OptionalDataRequest(
        request.request,
        request.request.user.internalId,
        request.request.user.eori,
        request.sessionId,
        _
      )
    }
}

trait DataRetrievalAction extends ActionTransformer[AuthenticatedRequestWithSessionId, OptionalDataRequest]
