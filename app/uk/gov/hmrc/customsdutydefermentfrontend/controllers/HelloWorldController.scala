package uk.gov.hmrc.customsdutydefermentfrontend.controllers

import uk.gov.hmrc.customsdutydefermentfrontend.views.html.HelloWorldPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class HelloWorldController @Inject()(
  mcc: MessagesControllerComponents,
  helloWorldPage: HelloWorldPage)
    extends FrontendController(mcc) {

  val helloWorld: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(helloWorldPage()))
  }

}
