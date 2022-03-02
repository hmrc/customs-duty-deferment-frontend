/*
 * Copyright 2022 HM Revenue & Customs
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
import mappings.{EditAddressDetailsFormProvider, EditContactDetailsFormProvider}
import models.responses.retrieve.ContactDetails
import models.{AddressDetailsUserAnswers, ContactDetailsUserAnswers, DataRequest}
import pages.{EditAddressDetailsPage, EditContactDetailsPage}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents, Result}
import services.{ContactDetailsCacheService, CountriesProviderService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contact_details.edit_address_details

import scala.concurrent.{ExecutionContext, Future}

class EditAddressDetailsController @Inject()(view: edit_address_details,
                                             identifier: IdentifierAction,
                                             dataRetrievalAction: DataRetrievalAction,
                                             dataRequiredAction: DataRequiredAction,
                                             resolveSessionId: SessionIdAction,
                                             userAnswersCache: UserAnswersCache,
                                             formProvider: EditAddressDetailsFormProvider,
                                             countriesProviderService: CountriesProviderService,
                                             contactDetailsCacheService: ContactDetailsCacheService,
                                             customsFinancialsApiConnector: CustomsFinancialsApiConnector)
                                            (implicit ec: ExecutionContext,
                                             errorHandler: ErrorHandler,
                                             mcc: MessagesControllerComponents,
                                             appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private val log = Logger(this.getClass)

  private def form: Form[AddressDetailsUserAnswers] = formProvider()

  private val commonActions: ActionBuilder[DataRequest, AnyContent] =
    identifier andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction

  def onPageLoad: Action[AnyContent] = commonActions async {
    implicit request =>
      request.userAnswers.get(EditAddressDetailsPage) match {
        case Some(contactDetails) =>
          Future.successful(Ok(view(contactDetails.dan, form.fill(contactDetails), countriesProviderService.countries)))
        case None =>
          log.error(s"Unable to retrieve stored account contact details")
          Future.successful(InternalServerError(errorHandler.standardErrorTemplate()))
      }
  }

  def submit: Action[AnyContent] = commonActions async { implicit request =>
    request.userAnswers.get(EditAddressDetailsPage) match {
      case Some(userAnswers) =>
        val form: Form[AddressDetailsUserAnswers] = formProvider.toForm(request.body.asFormUrlEncoded.get, userAnswers.dan)
        form.fold(
          (formWithErrors: Form[AddressDetailsUserAnswers]) => {
            Future.successful(
              BadRequest(view(userAnswers.dan, formWithErrors, countriesProviderService.countries))
            )
          },
          (updatedContactDetails: AddressDetailsUserAnswers) => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(EditAddressDetailsPage, updatedContactDetails))
              _ <- userAnswersCache.store(updatedAnswers.id, updatedAnswers)
              updateAddressDetails <- updateContactDetails(updatedContactDetails)
            } yield updateAddressDetails
          })
      case None =>
        Future(Redirect(routes.SessionExpiredController.onPageLoad))
    }
  }

  private def updateContactDetails(addressDetailsUserAnswers: AddressDetailsUserAnswers)
                                  (implicit request: DataRequest[AnyContent], hc: HeaderCarrier): Future[Result] = {
    (for {
      initialContactDetails <- contactDetailsCacheService.getContactDetails(request.identifier, addressDetailsUserAnswers.dan, request.eoriNumber)
      updatedContactDetails = contactDetailsWithUpdatedAddress(initialContactDetails, addressDetailsUserAnswers)
      _ <- customsFinancialsApiConnector.updateContactDetails(
        dan = addressDetailsUserAnswers.dan,
        eori = request.eoriNumber,
        oldContactDetails = initialContactDetails,
        newContactDetails = updatedContactDetails
      )
      _ <- contactDetailsCacheService.updateContactDetails(updatedContactDetails)
    } yield Redirect(routes.ConfirmContactDetailsController.success)).recover {
      case e =>
        log.error(s"Unable to update account contact details: ${e.getMessage}")
        Redirect(routes.ConfirmContactDetailsController.problem)
    }
  }

  def contactDetailsWithUpdatedAddress(oldContactDetails: ContactDetails, updatedAddressDetails: AddressDetailsUserAnswers): ContactDetailsUserAnswers = {
    ContactDetailsUserAnswers(dan = updatedAddressDetails.dan, name = oldContactDetails.contactName,
      addressLine1 = updatedAddressDetails.addressLine1, addressLine2 = updatedAddressDetails.addressLine2,
      addressLine3 = updatedAddressDetails.addressLine3, addressLine4 = updatedAddressDetails.addressLine4,
      postCode = updatedAddressDetails.postCode, countryCode = updatedAddressDetails.countryCode,
      countryName = updatedAddressDetails.countryName, telephone = oldContactDetails.telephone,
      fax = oldContactDetails.faxNumber, email = oldContactDetails.email)
  }
}