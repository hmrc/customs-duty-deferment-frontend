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

import config.AppConfig
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.Helpers._
import util.SpecBase
import views.html.session_expired

class SessionExpiredControllerSpec extends SpecBase {

  "onPageLoad" must {
    "return OK" in {
      val app: Application = application().build()
      val request = fakeRequest(GET, routes.SessionExpiredController.onPageLoad.url)

      val view = app.injector.instanceOf[session_expired]
      val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
      val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
      val messages: Messages = messagesApi.preferred(request)
      
      running(app) {
        val result = route(app, request).value
        status(result) mustBe OK
        contentAsString(result) mustBe view()(request, messages, appConfig).toString()
      }
    }
  }

}
