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

package controllers.actions

import config.AppConfig
import connectors.DataStoreConnector
import models.{AuthenticatedRequest, SignedInUser}
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter


import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IdentifierAction @Inject()(override val authConnector: AuthConnector,
                                 appConfig: AppConfig,
                                 val parser: BodyParsers.Default,
                                 dataStoreConnector: DataStoreConnector)(override implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent]
    with ActionRefiner[Request, AuthenticatedRequest]
    with AuthorisedFunctions {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised().retrieve(Retrievals.allEnrolments) { allEnrolments =>
        allEnrolments.getEnrolment("HMRC-CUS-ORG").flatMap(_.getIdentifier("EORINumber")) match {
          case Some(eori) =>
            for {
              allEoriHistory <- dataStoreConnector.getAllEoriHistory(eori.value)
              cdsLoggedInUser = SignedInUser(eori.value, allEoriHistory)
            } yield Right(AuthenticatedRequest(request, cdsLoggedInUser))
          case None => Future.successful(Left(Redirect(controllers.routes.NotSubscribedController.onPageLoad())))
        }
    }
  } recover {
    case _: NoActiveSession =>
      Left(Redirect(appConfig.loginUrl, Map("continue_url" -> Seq(appConfig.loginContinueUrl))))
    case _: InsufficientEnrolments =>
      Left(Redirect(controllers.routes.NotSubscribedController.onPageLoad()))
    case _ =>
      Left(Redirect(controllers.routes.NotSubscribedController.onPageLoad()))

  }
}




