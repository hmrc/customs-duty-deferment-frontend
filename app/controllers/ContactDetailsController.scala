package controllers

import cats.data.EitherT.{fromOption, fromOptionF, liftF}
import config.{AppConfig, ErrorHandler}
import connectors.SessionCacheConnector
import controllers.actions.{IdentifierAction, SessionIdAction}
import play.api.{Logger, LoggerLike}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import services.ContactDetailsService
import uk.gov.hmrc.http.HeaderCarrier
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

  val log: LoggerLike = Logger(this.getClass)

  def showContactDetails(linkId: String): Action[AnyContent] = (authenticate andThen resolveSessionId) async { implicit req =>
    val link = linkId.split('+').head
    (for {
      accountLink <- fromOptionF(getAccountLink(req.sessionId.value, link), NotFound(errorHandler.notFoundTemplate(req)))
      accountStatusId <- fromOption(accountLink.accountStatusId, NotFound(errorHandler.notFoundTemplate(req)))
      contactDetailsUrl <- liftF[Future, Result, String](contactDetailsService.getEncyptedDanWithStatus(accountLink.accountNumber,accountStatusId.value))
    } yield Redirect(contactDetailsUrl)).merge
      .recover { case _ => internalServerErrorFromContactDetails }
  }

  def internalServerErrorFromContactDetails(implicit request: Request[_]): Result = {
    log.error("InternalServerError from Contact Details")
    Ok(errorHandler.contactDetailsErrorTemplate())
  }

  private def getAccountLink(sessionId: String, linkId: String)(implicit hc:HeaderCarrier): Future[Option[AccountLink]] ={
    sessionCacheConnector.retrieveSession(sessionId, linkId)
  }
