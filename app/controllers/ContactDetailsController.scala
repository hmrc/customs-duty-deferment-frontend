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

package controllers

import cats.data.EitherT.{fromOption, fromOptionF, liftF}
import cats.instances.future._
import config.{AppConfig, ErrorHandler}
import connectors.SessionCacheConnector
import controllers.actions.{IdentifierAction, SessionIdAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.ContactDetailsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContactDetailsController @Inject()(authenticate: IdentifierAction,
                                         resolveSessionId: SessionIdAction,
                                         contactDetailsService: ContactDetailsService,
                                         sessionCacheConnector: SessionCacheConnector,
                                         implicit val mcc: MessagesControllerComponents)
                                        (implicit val appConfig: AppConfig,
                                         errorHandler: ErrorHandler,
                                         ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def showContactDetails(linkId: String): Action[AnyContent] =
    (authenticate andThen resolveSessionId) async { implicit req =>
      (for {
        accountLink <- fromOptionF(
          sessionCacheConnector.retrieveSession(req.sessionId.value, linkId),
          NotFound(errorHandler.notFoundTemplate(req))
        )
        accountStatusId <- fromOption(
          accountLink.accountStatusId,
          NotFound(errorHandler.notFoundTemplate(req))
        )
        contactDetailsUrl <- liftF[Future, Result, String](
          contactDetailsService.getEncyptedDanWithStatus(accountLink.accountNumber, accountStatusId.value)
        )
      } yield Redirect(contactDetailsUrl)).merge.recover {
        case _ => InternalServerError(errorHandler.contactDetailsErrorTemplate())
      }
    }
}
