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

package controllers

import cats.data.EitherT
import cats.data.EitherT.{fromOptionF, liftF}
import cats.instances.future._
import config.{AppConfig, ErrorHandler}
import connectors.{DataStoreConnector, SDDSConnector, SessionCacheConnector}
import controllers.actions.{IdentifierAction, SessionIdAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DirectDebitController @Inject()(authenticate: IdentifierAction,
                                      resolveSessionId: SessionIdAction,
                                      sddsConnector: SDDSConnector,
                                      dateStoreConnector: DataStoreConnector,
                                      sessionCacheConnector: SessionCacheConnector,
                                      errorHandler: ErrorHandler,
                                      appConfig: AppConfig,
                                      mcc: MessagesControllerComponents
                                     )(implicit ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def setup(linkId: String): Action[AnyContent] = (authenticate andThen resolveSessionId) async { implicit req =>
    val result: EitherT[Future, Result, Result] = (for {
      accountLink <- fromOptionF(
        sessionCacheConnector.retrieveSession(req.sessionId.value, linkId),
        Redirect(appConfig.financialsHomepage)
      )
      email <- fromOptionF(
        dateStoreConnector.getEmail(req.request.user.eori).map {
          case Right(email) => Some(email)
          case Left(_) => None
        }.recover { case _ => None },
        InternalServerError(errorHandler.sddsErrorTemplate())
      )
      directDebitSetupUrl <- liftF(sddsConnector.startJourney(accountLink.accountNumber, email.value))
    } yield Redirect(directDebitSetupUrl))

    result.merge.recover { case _ =>
      InternalServerError(errorHandler.sddsErrorTemplate())
    }
  }
}
