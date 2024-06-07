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

package viewmodels

import config.AppConfig
import controllers.routes
import models.{DDStatementType, FileFormat, FileRole}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase

import java.time.LocalDate

class DutyDefermentStatementStatementSpec extends SpecBase {

  "unavailableLinkHiddenText" must {

    "correctly give unavailable message for Supplementary statement type" in new Setup {
      val unavailbleText: String = period1.unavailableLinkHiddenText(FileFormat.Csv)(messages)
      unavailbleText.contains("unavailable") mustBe true
      unavailbleText.toLowerCase.contains("supplementary") mustBe true
    }

    "correctly give unavailable message for Excise statement type" in new Setup {
      val unavailbleText: String = period2.unavailableLinkHiddenText(FileFormat.Csv)(messages)
      unavailbleText.contains("unavailable") mustBe true
      unavailbleText.toLowerCase.contains("excise") mustBe true
    }

    "correctly give unavailable message for Weekly statement type" in new Setup {
      val unavailbleText: String = period3.unavailableLinkHiddenText(FileFormat.Csv)(messages)
      unavailbleText.contains("unavailable") mustBe true
    }

  }

  trait Setup {
    val year = 2023
    val month = 1
    val dayOfMonth01 = 1
    val dayOfMonth02 = 2
    val dayOfMonth30 = 30
    val dayOfMonth31 = 31

    val startDate01 = LocalDate.of(year, month, dayOfMonth01)
    val startDate02 = LocalDate.of(year, month, dayOfMonth02)
    val endDate01 = LocalDate.of(year, month, dayOfMonth30)
    val endDate02 = LocalDate.of(year, month, dayOfMonth31)

    val period1 = DutyDefermentStatementPeriod(FileRole.DutyDefermentStatement, DDStatementType.Supplementary,
      startDate01, startDate01, endDate01)

    val period2 = DutyDefermentStatementPeriod(FileRole.DutyDefermentStatement, DDStatementType.Excise,
      startDate01, startDate01, endDate01)

    val period3 = DutyDefermentStatementPeriod(FileRole.DutyDefermentStatement, DDStatementType.Weekly,
      startDate01, startDate01, endDate01)

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val request: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, routes.AccountController.showAccountDetails("someLink").url).withHeaders(
        "X-Session-Id" -> "someSessionId")

    val app: Application = application().overrides().build()
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
    val messages: Messages = messagesApi.preferred(request)
  }
}
