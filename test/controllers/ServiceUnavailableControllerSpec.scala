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

import navigation.Navigator
import play.api.Application
import play.api.http.Status.OK
import play.api.test.Helpers.{
  GET, contentAsString, defaultAwaitTimeout, route, running, status, writeableOf_AnyContentAsEmpty
}
import util.SpecBase
import views.html.service_unavailable

class ServiceUnavailableControllerSpec extends SpecBase {

  "onPageLoad" should {
    "render service unavailable page" in new Setup {
      running(applicationNew) {
        val request = fakeRequest(GET, routes.ServiceUnavailableController.onPageLoad("id-not-defined", "linkId_1").url)
        val result  = route(applicationNew, request).value

        status(result) mustBe OK
        contentAsString(result) mustBe view()(request, messages, appConfig).toString()
      }
    }

    "render service unavailable page for duty deferment statements page" in new Setup {
      running(applicationNew) {
        val request = fakeRequest(
          GET,
          routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementPageId, "linkId_1").url
        )
        val result  = route(applicationNew, request).value

        status(result) mustBe OK

        val backlink = Some(routes.AccountController.showAccountDetails("linkId_1").url)
        contentAsString(result) mustBe view(backlink)(request, messages, appConfig).toString()
      }
    }

    "render service unavailable page for duty deferment statements not available page" in new Setup {
      running(applicationNew) {
        val request = fakeRequest(
          GET,
          routes.ServiceUnavailableController.onPageLoad(navigator.dutyDefermentStatementNAPageId, "linkId_1").url
        )
        val result  = route(applicationNew, request).value

        status(result) mustBe OK
        val backlink = Some(routes.AccountController.statementsUnavailablePage("linkId_1").url)
        contentAsString(result) mustBe view(backlink)(request, messages, appConfig).toString()
      }
    }
  }

  trait Setup {
    val view: service_unavailable = instanceOf[service_unavailable]
    val navigator: Navigator      = new Navigator()

    val applicationNew: Application = applicationBuilder.build()
  }
}
