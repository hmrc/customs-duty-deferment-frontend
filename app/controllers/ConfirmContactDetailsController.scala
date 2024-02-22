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

import config.{AppConfig, ErrorHandler}
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction, SessionIdAction}
import pages.{EditAddressDetailsPage, EditContactDetailsPage}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contact_details.{edit_success_address, edit_success_contact}
import javax.inject.Inject
import services.AccountLinkCacheService

import scala.concurrent.{ExecutionContext, Future}

class ConfirmContactDetailsController @Inject()(successViewContact: edit_success_contact,
                                                successViewAddress: edit_success_address,
                                                identify: IdentifierAction,
                                                dataRetrievalAction: DataRetrievalAction,
                                                resolveSessionId: SessionIdAction,
                                                dataRequiredAction: DataRequiredAction,
                                                accountLinkCacheService : AccountLinkCacheService)
                                               (implicit ec: ExecutionContext,
                                                errorHandler: ErrorHandler,
                                                mcc: MessagesControllerComponents,
                                                appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with Logging {

  private val commonActions = identify andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction

  def successAddressDetails(): Action[AnyContent] = commonActions.async {
    implicit request => {
      request.userAnswers.get(EditAddressDetailsPage) match {
        case Some(userAnswers) =>
          val result = for {
            accLink <- accountLinkCacheService.get(request.userAnswers.id)
            accBool = accLink.map(_.isNiAccount).get
          } yield Ok(successViewAddress(userAnswers.dan, accBool))

          result.recover { case e =>
            logger.error(s"Call to account cache failed with exception=$e")
            InternalServerError(errorHandler.standardErrorTemplate())
          }
        case _ =>
          logger.error(s"Unable to get stored user answers whilst confirming account address details")
          Future.successful(InternalServerError(errorHandler.standardErrorTemplate()))
      }
    }
  }

  def successContactDetails(): Action[AnyContent] = commonActions.async {
    implicit request => {
        request.userAnswers.get(EditContactDetailsPage) match {
          case Some(userAnswers) =>
            val result = for {
              accLink <- accountLinkCacheService.get(request.userAnswers.id)
              accBool = accLink.map(_.isNiAccount).get
            } yield Ok(successViewContact(userAnswers.dan, accBool))

            result.recover { case e =>
              logger.error(s"Call to account cache failed with exception=$e")
              InternalServerError(errorHandler.standardErrorTemplate())
            }
          case None =>
            logger.error(s"Unable to get stored user answers whilst confirming account contact details")
            Future.successful(InternalServerError(errorHandler.standardErrorTemplate()))
        }
      }
    }

  def problem: Action[AnyContent] = identify async { implicit request =>
      Future {
        InternalServerError(errorHandler.errorUpdatingContactDetails)
      }
  }
}
