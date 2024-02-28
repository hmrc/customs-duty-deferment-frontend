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

import cache.UserAnswersCache
import config.{AppConfig, ErrorHandler}
import connectors.CustomsFinancialsApiConnector
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction, SessionIdAction}
import javax.inject.Inject
import mappings.EditContactDetailsFormProvider
import models.{DataRequest, EditContactDetailsUserAnswers}
import pages.EditContactDetailsPage
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents, Result}
import services.{AccountLinkCacheService, ContactDetailsCacheService, CountriesProviderService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contact_details.edit_contact_details

import scala.concurrent.{ExecutionContext, Future}

class EditContactDetailsController @Inject()(view: edit_contact_details,
                                             identifier: IdentifierAction,
                                             dataRetrievalAction: DataRetrievalAction,
                                             dataRequiredAction: DataRequiredAction,
                                             resolveSessionId: SessionIdAction,
                                             userAnswersCache: UserAnswersCache,
                                             formProvider: EditContactDetailsFormProvider,
                                             countriesProviderService: CountriesProviderService,
                                             contactDetailsCacheService: ContactDetailsCacheService,
                                             accountLinkCacheService: AccountLinkCacheService,
                                             customsFinancialsApiConnector: CustomsFinancialsApiConnector)
                                            (implicit ec: ExecutionContext,
                                             errorHandler: ErrorHandler,
                                             mcc: MessagesControllerComponents,
                                             appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private val log = Logger(this.getClass)

  private def form: Form[EditContactDetailsUserAnswers] = formProvider()

  private val commonActions: ActionBuilder[DataRequest, AnyContent] =
    identifier andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction

  def onPageLoad: Action[AnyContent] = commonActions async {
    implicit request =>
      request.userAnswers.get(EditContactDetailsPage) match {
        case Some(contactDetails) =>
          Future.successful(Ok(view(contactDetails.dan, contactDetails.isNiAccount,
            form.fill(contactDetails), countriesProviderService.countries)))
        case None =>
          log.error(s"Unable to retrieve stored account contact details")
          Future.successful(InternalServerError(errorHandler.standardErrorTemplate()))
      }
  }

  def submit: Action[AnyContent] = commonActions async { implicit request =>
    request.userAnswers.get(EditContactDetailsPage) match {
      case Some(userAnswers) =>
        val form: Form[EditContactDetailsUserAnswers] = formProvider.toForm(request.body.asFormUrlEncoded.get, userAnswers.dan)
        form.fold(
          (formWithErrors: Form[EditContactDetailsUserAnswers]) => {
            Future.successful(
              BadRequest(view(userAnswers.dan, userAnswers.isNiAccount,
                formWithErrors, countriesProviderService.countries))
            )
          },
          (updatedContactDetails: EditContactDetailsUserAnswers) => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(EditContactDetailsPage, updatedContactDetails))
              eori <- accountLinkCacheService.get(request.userAnswers.id).map(_.get.eori)
              _ <- userAnswersCache.store(updatedAnswers.id, updatedAnswers)
              updateAddressDetails <- updateContactDetailsUserAnswers(updatedContactDetails, eori)
            } yield updateAddressDetails
          })
      case None =>
        Future(Redirect(routes.SessionExpiredController.onPageLoad))
    }
  }

  private def updateContactDetailsUserAnswers(editContactDetailAnswers: EditContactDetailsUserAnswers,
                                              eori: String)
                                             (implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Result] = {
    (for {
      initialContactDetails <- contactDetailsCacheService.getContactDetails(request.identifier, editContactDetailAnswers.dan, request.eoriNumber)

      updatedContactDetails = editContactDetailAnswers.toContactDetailsUserAnswers(initialContactDetails,
        editContactDetailAnswers.isNiAccount, countriesProviderService.getCountryName)

      _ <- customsFinancialsApiConnector.updateContactDetails(
        dan = editContactDetailAnswers.dan,
        eori = eori,
        oldContactDetails = initialContactDetails,
        newContactDetails = updatedContactDetails
      )
      _ <- contactDetailsCacheService.updateContactDetails(updatedContactDetails)
    } yield Redirect(routes.ConfirmContactDetailsController.successContactDetails)).recover {
      case e =>
        log.error(s"Unable to update account contact details: ${e.getMessage}")
        Redirect(routes.ConfirmContactDetailsController.problem)
    }
  }
}
