package util

import controllers.actions.IdentifierAction
import models.{AuthenticatedRequest, SignedInUser}
import play.api.mvc.{AnyContent, BodyParser, PlayBodyParsers, Request, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeIdentifierAction @Inject()(bodyParsers: PlayBodyParsers) extends IdentifierAction {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, SignedInUser("exampleEori", Seq.empty)))

  override def parser: BodyParser[AnyContent] =
    bodyParsers.default

  override protected def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global
}