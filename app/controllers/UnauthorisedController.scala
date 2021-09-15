package controllers

import config.AppConfig
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.unauthorised

import javax.inject.Inject

class UnauthorisedController @Inject()(
                                        controllerComponents: MessagesControllerComponents,
                                        unauthorisedView: unauthorised
                                      )(implicit val appConfig: AppConfig) extends FrontendController(controllerComponents) with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action { implicit request =>
    Ok(unauthorisedView())
  }
}
