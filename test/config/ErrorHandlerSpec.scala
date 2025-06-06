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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import views.html.contact_details.edit_update_error
import views.html.{ErrorTemplate, not_found}

class ErrorHandlerSpec extends SpecBase {

  "overridden standardErrorTemplate" should {
    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = instanceOf[ErrorTemplate]

      errorHandler.standardErrorTemplate(title, heading, message).map { errorTemplate =>
        errorTemplate mustBe errorTemplateView(title, heading, message)

        val docView: Document = Jsoup.parse(errorTemplate.body)
        docView.getElementsByClass("govuk-heading-xl").text mustBe heading
        docView.getElementsByClass("govuk-body").text mustBe message
      }
    }
  }

  "notFoundTemplate" should {
    "display template with correct contents" in new Setup {
      val notFoundView: not_found = instanceOf[not_found]

      errorHandler.notFoundTemplate.map { notFoundTemplate =>
        notFoundTemplate.toString mustBe notFoundView.apply()(request, messages, appConfig).body
      }
    }
  }

  "unauthorized" should {
    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = instanceOf[ErrorTemplate]

      errorHandler.unauthorized() mustBe
        errorTemplateView.apply(
          messages("cf.error.unauthorized.title"),
          messages("cf.error.unauthorized.heading"),
          messages("cf.error.unauthorized.message")
        )
    }
  }

  "standardErrorTemplate" should {
    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = instanceOf[ErrorTemplate]

      errorHandler.standardErrorTemplate() mustBe
        errorTemplateView.apply(
          messages("accountDetails.edit.error.title"),
          messages("accountDetails.edit.error.heading"),
          messages("accountDetails.edit.error.message")
        )
    }
  }

  "sddsErrorTemplate" should {
    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = instanceOf[ErrorTemplate]

      errorHandler.sddsErrorTemplate() mustBe
        errorTemplateView.apply(
          messages("cf.error.standard-error-sdds.title"),
          messages("cf.error.standard-error-sdds.heading"),
          messages("cf.error.standard-error-sdds.message")
        )
    }
  }

  "contactDetailsErrorTemplate" should {
    "display template with correct contents" in new Setup {
      val errorTemplateView: ErrorTemplate = instanceOf[ErrorTemplate]

      errorHandler.contactDetailsErrorTemplate() mustBe
        errorTemplateView.apply(
          messages("cf.error.standard-error-contact-details.title"),
          messages("cf.error.standard-error-contact-details.heading"),
          messages("cf.error.standard-error-contact-details.message")
        )
    }
  }

  "errorUpdatingContactDetails" should {
    "display template with correct contents" in new Setup {
      val editUpdateTemplateView: edit_update_error = instanceOf[edit_update_error]

      errorHandler.errorUpdatingContactDetails() mustBe
        editUpdateTemplateView.apply(
          messages("accountDetails.edit.error.title"),
          messages("accountDetails.edit.error.heading"),
          messages("accountDetails.edit.error.message")
        )(request, messages, appConfig)
    }
  }

  trait Setup {
    implicit val ec: scala.concurrent.ExecutionContext        = scala.concurrent.ExecutionContext.global
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", "test_path")

    val errorHandler: ErrorHandler = instanceOf[ErrorHandler]
  }
}
