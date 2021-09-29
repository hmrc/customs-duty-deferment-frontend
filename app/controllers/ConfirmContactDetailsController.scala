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

import cache.UserAnswersCache
import config.{AppConfig, ErrorHandler}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction, SessionIdAction}
import pages.EditContactDetailsPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contact_details.edit_success

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmContactDetailsController @Inject()(successView: edit_success,
                                                identify: IdentifierAction,
                                                dataRetrievalAction: DataRetrievalAction,
                                                resolveSessionId: SessionIdAction,
                                                dataRequiredAction: DataRequiredAction,
                                                userAnswersCache: UserAnswersCache)
                                               (implicit ec: ExecutionContext,
                                                errorHandler: ErrorHandler,
                                                mcc: MessagesControllerComponents,
                                                appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with Logging {

  private val commonActions = identify andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction

  def success: Action[AnyContent] = commonActions.async {
    implicit request => {
      request.userAnswers.get(EditContactDetailsPage) match {
        case Some(userAnswers) =>
          userAnswersCache.remove(request.identifier).map {
            removeSuccessful =>
              if (!removeSuccessful) {
                logger.error("Failed to remove user answers from mongo")
              }
              Ok(successView(userAnswers.dan))
          }
        case None =>
          //TODO check error templates
          logger.error(s"Unable to get stored user answers whilst confirming account contact details")
          Future.successful(InternalServerError(errorHandler.contactDetailsErrorTemplate()))
      }
    }
  }

  def problem: Action[AnyContent] = identify async { implicit request =>
      Future {
        //TODO check error template
        val backLink = Some(appConfig.financialsHomepage)
        InternalServerError(errorHandler.contactDetailsErrorTemplate())
      }
  }

}