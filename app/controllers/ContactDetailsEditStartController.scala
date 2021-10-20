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
import cats.data.EitherT
import cats.data.EitherT.{fromOption, fromOptionF, liftF}
import cats.instances.future._
import config.{AppConfig, ErrorHandler}
import controllers.actions.{IdentifierAction, SessionIdAction}
import models.responses.retrieve.ContactDetails
import models.{ContactDetailsUserAnswers, DutyDefermentAccountLink, UserAnswers}
import pages.EditContactDetailsPage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class ContactDetailsEditStartController @Inject()(
  contactDetailsCacheService: ContactDetailsCacheService,
  dateTimeService: DateTimeService,
  identifier: IdentifierAction,
  resolveSessionId: SessionIdAction,
  accountLinkCacheService: AccountLinkCacheService,
  errorHandler: ErrorHandler,
  countriesProviderService: CountriesProviderService,
  appConfig: AppConfig,
  userAnswersCache: UserAnswersCache,
  mcc: MessagesControllerComponents
                                                 )(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with Logging {

  def start: Action[AnyContent] = (identifier andThen resolveSessionId) async {
    implicit request =>
      val futureResponse: EitherT[Future, Result, Result] = for {
        dutyDefermentDetails <- fromOptionF(
          accountLinkCacheService.get(request.request.user.internalId),
          Redirect(appConfig.financialsHomepage)
        )
        initialContactDetails <- liftF(
          contactDetailsCacheService.getContactDetails(
            request.request.user.internalId,
            dutyDefermentDetails.dan,
            request.request.user.eori)
        )
        initialUserAnswers <- fromOption(
          setUserAnswers(initialContactDetails, dutyDefermentDetails, request.request.user.internalId), {
            logger.error(s"Unable to store user answers")
            InternalServerError(errorHandler.contactDetailsErrorTemplate())
          }
        )
        _ <- liftF(userAnswersCache.store(initialUserAnswers.id, initialUserAnswers))
      } yield Redirect(routes.EditContactDetailsController.onPageLoad())

      futureResponse
        .merge
        .recover {
          case NonFatal(e) =>
            logger.error(s"Unable to retrieve account contact details: ${e.getMessage}")
            InternalServerError(errorHandler.contactDetailsErrorTemplate())
        }
  }

  private def setUserAnswers(initialContactDetails: ContactDetails, dutyDefermentDetails: DutyDefermentAccountLink, internalId: String): Option[UserAnswers] = {
    val initialUserAnswers = ContactDetailsUserAnswers.fromContactDetails(
      dan = dutyDefermentDetails.dan,
      contactDetails = initialContactDetails,
      getCountryNameF = countriesProviderService.getCountryName)

    UserAnswers(internalId, lastUpdated = dateTimeService.now())
      .set(EditContactDetailsPage, initialUserAnswers).toOption
  }
}