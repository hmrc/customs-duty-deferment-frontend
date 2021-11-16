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

import config.{AppConfig, ErrorHandler}
import connectors.CustomsFinancialsApiConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction, SessionIdAction}
import models.{ContactDetailsUserAnswers, DataRequest}
import pages.EditContactDetailsPage
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contact_details.check_answers

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckAnswersContactDetailsController @Inject()(checkAnswersView: check_answers,
                                                     identifier: IdentifierAction,
                                                     resolveSessionId: SessionIdAction,
                                                     dataRetrievalAction: DataRetrievalAction,
                                                     dataRequiredAction: DataRequiredAction,
                                                     contactDetailsCacheService: ContactDetailsCacheService,
                                                     customsFinancialsApiConnector: CustomsFinancialsApiConnector)
                                                    (implicit ec: ExecutionContext,
                                                     errorHandler: ErrorHandler,
                                                     mcc: MessagesControllerComponents,
                                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private val log = Logger(this.getClass)

  private val commonActions = identifier andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction

  def onPageLoad: Action[AnyContent] = commonActions {
    implicit request =>
      request.userAnswers.get(EditContactDetailsPage) match {
        case Some(updatedContactDetails) =>
          Ok(checkAnswersView(updatedContactDetails))
        case None =>
          log.error(s"Unable to retrieve stored account contact details")
          InternalServerError(errorHandler.contactDetailsErrorTemplate)
      }
  }

  def submit: Action[AnyContent] = commonActions async { implicit request =>
    request.userAnswers.get(EditContactDetailsPage) match {
      case Some(answers) => updateContactDetails(answers)
      case None =>
        log.error(s"Unable to get stored user answers whilst updating account contact details")
        Future.successful(InternalServerError(errorHandler.contactDetailsErrorTemplate()))
    }
  }

  private def updateContactDetails(contactDetailsUserAnswers: ContactDetailsUserAnswers)
                                  (implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Result] = {
    (for {
      initialContactDetails <- contactDetailsCacheService.getContactDetails(request.identifier, contactDetailsUserAnswers.dan, request.eoriNumber)
      _ <- customsFinancialsApiConnector.updateContactDetails(
        dan = contactDetailsUserAnswers.dan,
        eori = request.eoriNumber,
        oldContactDetails = initialContactDetails,
        newContactDetails = contactDetailsUserAnswers
      )
      _ <- contactDetailsCacheService.updateContactDetails(contactDetailsUserAnswers)
    } yield Redirect(routes.ConfirmContactDetailsController.success)).recover {
      case e =>
        log.error(s"Unable to update account contact details: ${e.getMessage}")
        Redirect(routes.ConfirmContactDetailsController.problem)
    }
  }

}