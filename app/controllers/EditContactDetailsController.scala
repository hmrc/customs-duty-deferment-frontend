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
import mappings.EditContactDetailsFormProvider
import models.ContactDetailsUserAnswers
import pages.EditContactDetailsPage
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CountriesProviderService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.contact_details.edit

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EditContactDetailsController @Inject()(view: edit,
                                             identifier: IdentifierAction,
                                             dataRetrievalAction: DataRetrievalAction,
                                             dataRequiredAction: DataRequiredAction,
                                             resolveSessionId: SessionIdAction,
                                             userAnswersCache: UserAnswersCache,
                                             formProvider: EditContactDetailsFormProvider,
                                             countriesProviderService: CountriesProviderService)
                                            (implicit ec: ExecutionContext,
                                             errorHandler: ErrorHandler,
                                             mcc: MessagesControllerComponents,
                                             appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {

  private val log = Logger(this.getClass)

  private def form: Form[ContactDetailsUserAnswers] = formProvider()

  def onPageLoad: Action[AnyContent] = identifier andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction async {
    implicit request =>
      Future(
        request.userAnswers.get(EditContactDetailsPage) match {
          case Some(contactDetails) =>
            //TODO implement linkId
            Ok(view(contactDetails.dan, "NO-LINK-ID", form.fill(contactDetails), countriesProviderService.countries))
          case None =>
            //TODO review error template
            log.error(s"Unable to retrieve stored account contact details")
            InternalServerError(errorHandler.contactDetailsErrorTemplate())
        }
      )
  }

  def submit: Action[AnyContent] = identifier andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction async { implicit request =>
    request.userAnswers.get(EditContactDetailsPage) match {
      case Some(userAnswers) =>
        val form: Form[ContactDetailsUserAnswers] = formProvider.toForm(request.body.asFormUrlEncoded.get, userAnswers.dan)
        form.fold(
          (formWithErrors: Form[ContactDetailsUserAnswers]) => {
            Future.successful(
              //TODO needs linkId maybe
              BadRequest(view(userAnswers.dan, "fakeLinkId", formWithErrors, countriesProviderService.countries))
            )
          },
          (updatedContactDetails: ContactDetailsUserAnswers) => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(EditContactDetailsPage, updatedContactDetails))
              _ <- userAnswersCache.store(updatedAnswers.id, updatedAnswers)
            } yield Redirect(routes.CheckAnswersContactDetailsController.onPageLoad())
          })
      case None =>
        Future(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
  }

}