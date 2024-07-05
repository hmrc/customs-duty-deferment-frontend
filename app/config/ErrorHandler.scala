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

package config

import javax.inject.{Inject, Singleton}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import views.html.{ErrorTemplate, not_found}
import views.html.contact_details.edit_update_error

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class ErrorHandler @Inject()(errorTemplate: ErrorTemplate,
                             notFound: not_found,
                             val messagesApi: MessagesApi,
                             editUpdateError: edit_update_error)
                            (implicit appConfig: AppConfig, protected val ec: ExecutionContext)
  extends FrontendErrorHandler {

  override def standardErrorTemplate(pageTitle: String,
                                     heading: String,
                                     message: String)(using request: RequestHeader): Future[Html] =
    Future.successful(errorTemplate(pageTitle, heading, message))

  override def notFoundTemplate(using request: RequestHeader): Future[Html] =
    Future.successful(notFound())

  def unauthorized()(implicit request: RequestHeader): Html = {
    errorTemplate(
      Messages("cf.error.unauthorized.title"),
      Messages("cf.error.unauthorized.heading"),
      Messages("cf.error.unauthorized.message")
    )
  }

  def standardErrorTemplate()(implicit rh:RequestHeader): Html = {
    errorTemplate(
      Messages("accountDetails.edit.error.title"),
      Messages("accountDetails.edit.error.heading"),
      Messages("accountDetails.edit.error.message")
    )
  }

  def sddsErrorTemplate()(implicit request: RequestHeader): Html =
    errorTemplate(
      Messages("cf.error.standard-error-sdds.title"),
      Messages("cf.error.standard-error-sdds.heading"),
      Messages("cf.error.standard-error-sdds.message")
    )

  def contactDetailsErrorTemplate()(implicit request: RequestHeader): Html =
    errorTemplate(
      Messages("cf.error.standard-error-contact-details.title"),
      Messages("cf.error.standard-error-contact-details.heading"),
      Messages("cf.error.standard-error-contact-details.message")
    )

  def errorUpdatingContactDetails()(implicit rh: RequestHeader): Html = {
    editUpdateError(
      title = Messages("accountDetails.edit.error.title"),
      heading = Messages("accountDetails.edit.error.heading"),
      message = Messages("accountDetails.edit.error.message")
    )
  }
}
