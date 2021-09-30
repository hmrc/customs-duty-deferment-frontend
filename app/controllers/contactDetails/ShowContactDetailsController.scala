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

package controllers.contactDetails

import com.google.inject.Inject
import config.AppConfig
import controllers.actions.{AuthenticatedRequestWithSessionId, IdentifierAction, SessionIdAction}
import controllers.routes
import models.DutyDefermentDetails
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{ContactDetailsCacheService, CountriesProviderService, DutyDefermentCacheService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.ContactDetailsViewModel
import views.html.contact_details.{show, show_error}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class ShowContactDetailsController @Inject()(mcc: MessagesControllerComponents,
                                             view: show,
                                             errorView: show_error,
                                             identifier: IdentifierAction,
                                             resolveSessionId: SessionIdAction,
                                             dutyDefermentCacheService: DutyDefermentCacheService,
                                             contactDetailsCacheService: ContactDetailsCacheService,
                                             countriesProviderService: CountriesProviderService)
                                            (implicit ec: ExecutionContext, appConfig: AppConfig)

  extends FrontendController(mcc) with I18nSupport {

  private val log = Logger(this.getClass)

  def showActiveSession(): Action[AnyContent] = identifier andThen resolveSessionId async { implicit request =>
    dutyDefermentCacheService.get(request.request.user.internalId).flatMap {
      case None => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      case Some(details) => process(details)
    }
  }

  def show(linkId: String): Action[AnyContent] = identifier andThen resolveSessionId async { implicit request =>
    dutyDefermentCacheService.getAndCache(linkId, request.sessionId.value, request.request.user.internalId).flatMap {
      case Left(_) => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      case Right(details) => process(details)
    }
  }

  private def process(details: DutyDefermentDetails)(implicit request: AuthenticatedRequestWithSessionId[AnyContent]): Future[Result] = {
    (for {
      contactDetails <- contactDetailsCacheService.getContactDetails(
        request.request.user.internalId,
        details.dan,
        request.request.user.eori
      )
      viewModel = ContactDetailsViewModel(details.dan, contactDetails, countriesProviderService.getCountryName)
    } yield {
      Ok(view(viewModel, details.statusId, details.linkId))
    }).recover {
      case NonFatal(e) =>
        log.error(s"Unable to retrieve account details: ${e.getMessage}")
        InternalServerError(errorView(details.dan, Option(appConfig.financialsHomepage), details.statusId, details.linkId))
    }
  }
}