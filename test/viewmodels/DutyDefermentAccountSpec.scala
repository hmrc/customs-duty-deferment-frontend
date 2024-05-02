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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.Application
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase
import views.html.duty_deferment_account.duty_deferment_account

import java.time.LocalDate

class DutyDefermentAccountSpec extends SpecBase {

  "DutyDefermentAccount" should {
    "Return a max of 5 statements when number of months is greater than 5" in new Setup {

      val emptyPeriods: Seq[DutyDefermentStatementPeriod] = Seq.empty
      val testStatement = DutyDefermentStatementPeriodsByMonth(LocalDate.now(), emptyPeriods)

      val testStatements: Seq[DutyDefermentStatementPeriodsByMonth] = Seq(
        testStatement,
        testStatement,
        testStatement,
        testStatement,
        testStatement,
        testStatement,
        testStatement,
        testStatement)

      val result = model.dropOldMonths(testStatements)
      result.size mustBe 5
    }

    "Return a number below 5 when the number of statements is below 5" in new Setup {

      val emptyPeriods: Seq[DutyDefermentStatementPeriod] = Seq.empty
      val testStatement = DutyDefermentStatementPeriodsByMonth(LocalDate.now(), emptyPeriods)

      val testStatements: Seq[DutyDefermentStatementPeriodsByMonth] = Seq(
        testStatement,
        testStatement)

      val result = model.dropOldMonths(testStatements)
      result.size mustBe 2

    }

    "Return correct month when applied to DutyDeferementAccount monthsToDisplay" in new Setup {
      val numMonths = 6
      val now: Int = LocalDate.now().minusMonths(numMonths).getMonthValue

      now mustBe model.monthsToDisplay.getMonthValue
    }
  }

  trait Setup {
    val app: Application = application().build()
    val serviceUnavailableUrl: Option[String] = Option("service_unavailable_url")
    val accountNumber = "1234567"
    val linkId = "link_id"

    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    implicit val msg: Messages = messages(app)
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val model: DutyDefermentAccount = DutyDefermentAccount(
      accountNumber,
      Seq(dutyDefermentStatementsForEori),
      "linkId",
      isNiAccount = false)

    val view: Document = Jsoup.parse(
      app.injector.instanceOf[duty_deferment_account].apply(model, serviceUnavailableUrl).body)
  }
}
