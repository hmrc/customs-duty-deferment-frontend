package controllers

import cats.data.EitherT
import cats.data.EitherT.{fromOptionF, liftF}
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
                                      implicit val mcc: MessagesControllerComponents)
                                     (implicit val appConfig: AppConfig,
                                      errorHandler: ErrorHandler,
                                      ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def setup(linkId: String): Action[AnyContent] = (authenticate andThen resolveSessionId) async { implicit req =>
    val result: EitherT[Future, Result, Result] = for {
      accountLink <- fromOptionF(
        sessionCacheConnector.retrieveSession(req.sessionId.value, linkId),
        NotFound(errorHandler.notFoundTemplate(req))
      )
      email <- fromOptionF(
        dateStoreConnector.getEmail(req.request.user.eori),
        InternalServerError(errorHandler.sddsErrorTemplate())
      )
      directDebitSetupUrl <- liftF(sddsConnector.startJourney(
        appConfig.financialsHomepage,
        appConfig.financialsHomepage,
        accountLink.accountNumber,
        email.value)
      )
    } yield Redirect(directDebitSetupUrl)

    result.merge.recover {
      case _ =>
        InternalServerError(errorHandler.sddsErrorTemplate())
    }
  }
}
