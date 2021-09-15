package controllers

import connectors.{FinancialsApiConnector, SessionCacheConnector}
import controllers.actions.{IdentifierAction, SessionIdAction}
import models.FileRole.DutyDefermentStatement
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.api.{Logger, LoggerLike}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import cats.data.EitherT._
import cats.instances.future._
import config.AppConfig

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class AccountController @Inject()(
                                   val authenticate: IdentifierAction,
                                   apiConnector: FinancialsApiConnector,
                                   resolveSessionId: SessionIdAction,
                                   mcc: MessagesControllerComponents,
                                   sessionCacheConnector: SessionCacheConnector,
                                   appConfig: AppConfig
                                 )(implicit executionContext: ExecutionContext) extends FrontendController(mcc) {

  val log: LoggerLike = Logger(this.getClass)

  def showAccountDetails(linkId: String): Action[AnyContent] = (authenticate andThen resolveSessionId) async { implicit req =>
    apiConnector.deleteNotification(req.request.eori, DutyDefermentStatement)

    (for {
      accountLink <- fromOptionF(
        sessionCacheConnector.retrieveSession(req.sessionId.value, linkId),
        Redirect(appConfig.financialsHomepage)
      )
      historicEoris = req.user.allEoriHistory
      statementsForEoris <- liftF[Future, Result, Seq[DutyDefermentStatementsForEori]](Future.sequence(
        historicEoris.map(historicEori => statementsFromEoriHistory(historicEori, accountLink))
      ))
    } yield {
      val dutyDefermentViewModel = DutyDefermentAccount(accountLink.accountNumber, statementsForEoris, accountLink.linkId)
      Ok(dutyDefermentAccountView(dutyDefermentViewModel))
    }
      ).merge.recover {
      case NonFatal(e) => {
        log.error(s"Unable to retrieve Duty deferment statements :${e.getMessage}")
        Redirect(routes.DutyDefermentAccountController.statementsUnavailablePage(linkId))
      }
    }
  }
}
