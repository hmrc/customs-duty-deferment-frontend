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

import config.AppConfig
import connectors.SessionCacheConnector
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LogoutController @Inject()(sessionCacheConnector: SessionCacheConnector, mcc: MessagesControllerComponents)
                                (implicit val appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) {

  val feedbackLink: String = appConfig.feedbackService

  def logout: Action[AnyContent] = Action async { implicit request => {
    hc.sessionId match {
      case Some(id) => sessionCacheConnector.removeSession(id.value).map(_ => Redirect(appConfig.signOutUrl, Map("continue" -> Seq(feedbackLink))))
      case None => Future.successful(Redirect(appConfig.signOutUrl, Map("continue" -> Seq(feedbackLink))))
    }
  }
  }

  def logoutNoSurvey: Action[AnyContent] = Action async { implicit request => {
    hc.sessionId match {
      case Some(id) => sessionCacheConnector.removeSession(id.value).map(_ => Results.Redirect(appConfig.signOutUrl, Map("continue" -> Seq(appConfig.loginContinueUrl))))
      case None => Future.successful(Redirect(appConfig.signOutUrl, Map("continue" -> Seq(appConfig.loginContinueUrl))))
    }
  }
  }
}
