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

import connectors.{FinancialsApiConnector, SessionCacheConnector}
import controllers.actions.{AuthenticatedRequestWithSessionId, IdentifierAction, SessionIdAction}
import models.FileRole.DutyDefermentStatement
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.api.{Logger, LoggerLike}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import cats.data.EitherT._
import cats.instances.future._
import config.{AppConfig, ErrorHandler}
import models.{AccountLink, EoriHistory}
import play.api.i18n.I18nSupport
import services.DocumentService
import viewmodels.{DutyDefermentAccount, DutyDefermentStatementsForEori}
import views.html.{duty_deferment_account, duty_deferment_statements_not_available}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class AccountController @Inject()(
                                   val authenticate: IdentifierAction,
                                   apiConnector: FinancialsApiConnector,
                                   resolveSessionId: SessionIdAction,
                                   mcc: MessagesControllerComponents,
                                   sessionCacheConnector: SessionCacheConnector,
                                   documentService: DocumentService,
                                   account: duty_deferment_account,
                                   unavailable: duty_deferment_statements_not_available,
                                   errorHandler: ErrorHandler
                                 )(implicit executionContext: ExecutionContext, appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  val log: LoggerLike = Logger(this.getClass)

  def showAccountDetails(linkId: String): Action[AnyContent] = (authenticate andThen resolveSessionId) async { implicit req =>
    apiConnector.deleteNotification(req.request.user.eori, DutyDefermentStatement)
    (for {
      accountLink <- fromOptionF(
        sessionCacheConnector.retrieveSession(req.sessionId.value, linkId),
        Redirect(appConfig.financialsHomepage)
      )
      historicEoris = req.request.user.allEoriHistory
      statementsForEoris <- liftF[Future, Result, Seq[DutyDefermentStatementsForEori]](Future.sequence(
        historicEoris.map(historicEori => statementsFromEoriHistory(historicEori, accountLink))
      ))
    } yield {
      val dutyDefermentViewModel = DutyDefermentAccount(accountLink.accountNumber, statementsForEoris, accountLink.linkId)
      Ok(account(dutyDefermentViewModel))
    }
      ).merge.recover {
      case NonFatal(e) => {
        log.error(s"Unable to retrieve Duty deferment statements :${e.getMessage}")
        Redirect(routes.AccountController.statementsUnavailablePage(linkId))
      }
    }
  }

  def statementsUnavailablePage(linkId: String): Action[AnyContent] = (authenticate andThen resolveSessionId) async { implicit req =>
    val eventualMaybeAccountLink = sessionCacheConnector.retrieveSession(req.sessionId.value, linkId)
    eventualMaybeAccountLink.map {
      accountLink => accountLink.fold(Unauthorized(errorHandler.unauthorized()))(link => Ok(unavailable(link.accountNumber, linkId)))
    }
  }

  private def statementsFromEoriHistory(eoriHistory: EoriHistory, accountLink: AccountLink)(implicit req: AuthenticatedRequestWithSessionId[_]): Future[DutyDefermentStatementsForEori] = {
    documentService.getDutyDefermentStatements(eoriHistory.eori, accountLink.accountNumber)
      .map(_.partition(_.metadata.statementRequestId.isEmpty))
      .map {
        case (current, requested) => DutyDefermentStatementsForEori(eoriHistory, current, requested)
      }
  }
}
