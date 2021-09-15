package controllers.actions

import com.google.inject.Inject
import config.ErrorHandler
import models.IdentifierRequest
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

case class AuthenticatedRequestWithSessionId[A](request: IdentifierRequest[A], sessionId: SessionId)
  extends WrappedRequest[A](request)


class SessionIdAction @Inject()()(implicit val executionContext: ExecutionContext, errorHandler: ErrorHandler)
  extends ActionRefiner[IdentifierRequest, AuthenticatedRequestWithSessionId] {

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, AuthenticatedRequestWithSessionId[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    hc.sessionId match {
      case None => Future.successful(Left(Unauthorized(errorHandler.unauthorized()(request))))
      case Some(sessionId) => Future.successful(Right(AuthenticatedRequestWithSessionId(request, sessionId)))
    }
  }
}