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

import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import views.html.{ErrorTemplate, not_found}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.contact_details.edit_update_error

class ErrorHandlerSpec extends SpecBase {

  "overridden standardErrorTemplate" should {

    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = app.injector.instanceOf[ErrorTemplate]

      errorHandler.standardErrorTemplate(title, heading, message).map {
        errorTemplate =>
          errorTemplate mustBe errorTemplateView(title, heading, message)

          val docView: Document = Jsoup.parse(errorTemplate.body)
          docView.getElementsByClass("govuk-heading-xl").text mustBe heading
          docView.getElementsByClass("govuk-body").text mustBe message
      }
    }
  }

  "notFoundTemplate" should {

    "display template with correct contents" in new Setup {
      val notFoundView: not_found = app.injector.instanceOf[not_found]

      errorHandler.notFoundTemplate.map {
        notFoundTemplate => notFoundTemplate.toString mustBe notFoundView.apply().body
      }
    }
  }

  "unauthorized" should {

    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = app.injector.instanceOf[ErrorTemplate]

      errorHandler.unauthorized() mustBe
        errorTemplateView.apply(
          msgs("cf.error.unauthorized.title"),
          msgs("cf.error.unauthorized.heading"),
          msgs("cf.error.unauthorized.message")
        )
    }
  }

  "standardErrorTemplate" should {

    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = app.injector.instanceOf[ErrorTemplate]

      errorHandler.standardErrorTemplate() mustBe
        errorTemplateView.apply(
          msgs("accountDetails.edit.error.title"),
          msgs("accountDetails.edit.error.heading"),
          msgs("accountDetails.edit.error.message")
        )
    }
  }

  "sddsErrorTemplate" should {

    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = app.injector.instanceOf[ErrorTemplate]

      errorHandler.sddsErrorTemplate() mustBe
        errorTemplateView.apply(
          msgs("cf.error.standard-error-sdds.title"),
          msgs("cf.error.standard-error-sdds.heading"),
          msgs("cf.error.standard-error-sdds.message")
        )
    }
  }

  "contactDetailsErrorTemplate" should {

    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = app.injector.instanceOf[ErrorTemplate]

      errorHandler.contactDetailsErrorTemplate() mustBe
        errorTemplateView.apply(
          msgs("cf.error.standard-error-contact-details.title"),
          msgs("cf.error.standard-error-contact-details.heading"),
          msgs("cf.error.standard-error-contact-details.message")
        )
    }
  }

  "errorUpdatingContactDetails" should {

    "display template with correct contents" in new Setup {
      val editUpdateTemplateView: edit_update_error = app.injector.instanceOf[edit_update_error]

      errorHandler.errorUpdatingContactDetails() mustBe
        editUpdateTemplateView.apply(
          msgs("accountDetails.edit.error.title"),
          msgs("accountDetails.edit.error.heading"),
          msgs("accountDetails.edit.error.message")
        )
    }
  }

  trait Setup {
    val app: Application = application().build()

    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", "test_path")
    implicit val msgs: Messages = messages(app)

    val errorHandler: ErrorHandler = app.injector.instanceOf[ErrorHandler]
    val title = "test_title"
    val heading = "test_heading"
    val message = "test_msg"
  }
}
