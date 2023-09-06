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

import cats.data.EitherT._
import cats.instances.future._
import config.{AppConfig, ErrorHandler}
import connectors.{CustomsFinancialsApiConnector, SessionCacheConnector}
import controllers.actions.{IdentifierAction, SessionIdAction}
import models.FileRole.{DutyDefermentStatement}
import navigation.Navigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.DocumentService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.{DutyDefermentAccount, DutyDefermentStatementsForEori}
import views.html.duty_deferment_account.{duty_deferment_account, duty_deferment_statements_not_available}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccountController @Inject()(
                                   val authenticate: IdentifierAction,
                                   apiConnector: CustomsFinancialsApiConnector,
                                   resolveSessionId: SessionIdAction,
                                   mcc: MessagesControllerComponents,
                                   sessionCacheConnector: SessionCacheConnector,
                                   documentService: DocumentService,
                                   account: duty_deferment_account,
                                   unavailable: duty_deferment_statements_not_available,
                                   errorHandler: ErrorHandler,
                                   navigator: Navigator
                                 )(implicit executionContext: ExecutionContext, appConfig: AppConfig) extends
  FrontendController(mcc) with I18nSupport {

  def showAccountDetails(linkId: String): Action[AnyContent] =
    (authenticate andThen resolveSessionId) async { implicit req =>
      apiConnector.deleteNotification(req.request.user.eori, DutyDefermentStatement)
      (for {
        accountLink <- fromOptionF(
          sessionCacheConnector.retrieveSession(req.sessionId.value, linkId),
          Redirect(appConfig.financialsHomepage)
        )
        historicEoris = req.request.user.allEoriHistory
        statementsForEoris <- liftF[Future, Result, Seq[DutyDefermentStatementsForEori]](Future.sequence(
          historicEoris.map(historicEori =>
            documentService.getDutyDefermentStatements(historicEori, accountLink.accountNumber)
          )))
      } yield {
        val dutyDefermentViewModel = DutyDefermentAccount(accountLink.accountNumber,
          statementsForEoris, accountLink.linkId, accountLink.isNiAccount)

        val historicUrl = if(appConfig.historicStatementsEnabled) {
          appConfig.historicRequestUrl(accountLink.linkId)
        } else {
          routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementPageId, linkId).url
        }

        Ok(account(
          dutyDefermentViewModel,
          Some(historicUrl))
        )
      }
        ).merge.recover {
        case _ => Redirect(routes.AccountController.statementsUnavailablePage(linkId))
      }
    }

  def statementsUnavailablePage(linkId: String): Action[AnyContent] =
    (authenticate andThen resolveSessionId).async { implicit req =>
      sessionCacheConnector.retrieveSession(req.sessionId.value, linkId).map {
        case Some(link) => Ok(
          unavailable(link.accountNumber,
            linkId,
            Some(routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementNAPageId, linkId).url)))
        case None => Unauthorized(errorHandler.unauthorized())
      }
    }
}
