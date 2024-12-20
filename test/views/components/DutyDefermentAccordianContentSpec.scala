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

package views.components

import config.AppConfig
import models.{DDStatementType, FileRole}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.api.Application
import play.api.i18n.Messages
import util.SpecBase
import viewmodels.{DutyDefermentStatementPeriod, DutyDefermentStatementPeriodsByMonth}
import views.html.components.duty_deferment_accordian_content

import java.time.LocalDate

class DutyDefermentAccordianContentSpec extends SpecBase {

  "view" should {

    "display ExciseDeferment record with correct file description" in new Setup {
      val viewDoc: Document = view(model01)
      val htmlText: String  = viewDoc.body().text()

      shouldContainExciseDefermentText(htmlText)
      shouldNotContainDutyDefermentText(htmlText)
      shouldNotContainExciseSummaryText(htmlText)
      shouldNotContainSupplementaryText(htmlText)
      shouldContainCsvFileDesc(htmlText)
      shouldContainPdfFileDesc(htmlText)
    }

    "display DutyDeferment record with correct file descriptions" in new Setup {
      val viewDoc: Document = view(model02)
      val htmlText: String  = viewDoc.body().text()

      shouldContainDutyDefermentText(htmlText)
      shouldNotContainExciseDefermentText(htmlText)
      shouldNotContainExciseSummaryText(htmlText)
      shouldNotContainSupplementaryText(htmlText)
      shouldNotContainCsvFileDesc(htmlText)
      shouldContainPdfFileDesc(htmlText)
    }

    "display Supplementary record with correct file descriptions" in new Setup {
      val viewDoc: Document = view(model03)
      val htmlText: String  = viewDoc.body().text()

      shouldContainSupplementaryText(htmlText)
      shouldNotContainDutyDefermentText(htmlText)
      shouldNotContainExciseDefermentText(htmlText)
      shouldNotContainExciseSummaryText(htmlText)
      shouldNotContainCsvFileDesc(htmlText)
      shouldContainPdfFileDesc(htmlText)
    }

    "display DutyDeferment and Weekly record with correct file descriptions" in new Setup {
      val viewDoc: Document = view(model04)
      val htmlText: String  = viewDoc.body().text()

      shouldContainDutyDefermentText(htmlText)
      shouldNotContainExciseDefermentText(htmlText)
      shouldNotContainExciseSummaryText(htmlText)
      shouldNotContainSupplementaryText(htmlText)
      shouldContainCsvFileDesc(htmlText)
      shouldContainPdfFileDesc(htmlText)
    }
  }

  private def shouldContainCsvFileDesc(htmlText: String): Assertion =
    htmlText.contains("CSV (1KB)") mustBe true

  private def shouldNotContainCsvFileDesc(htmlText: String): Assertion =
    htmlText.contains("CSV (1KB)") mustBe false

  private def shouldContainPdfFileDesc(htmlText: String): Assertion =
    htmlText.contains("PDF (1KB)") mustBe true

  private def shouldContainSupplementaryText(htmlText: String): Assertion =
    htmlText.contains("Supplementary end of month") mustBe true

  private def shouldNotContainSupplementaryText(htmlText: String): Assertion =
    htmlText.contains("Supplementary end of month") mustBe false

  private def shouldNotContainExciseSummaryText(htmlText: String): Assertion =
    htmlText.contains("Excise summary") mustBe false

  private def shouldContainDutyDefermentText(htmlText: String): Assertion =
    htmlText.contains("Duty deferment 1720") mustBe true

  private def shouldNotContainDutyDefermentText(htmlText: String): Assertion =
    htmlText.contains("Duty deferment 1720") mustBe false

  private def shouldContainExciseDefermentText(htmlText: String): Assertion =
    htmlText.contains("Excise deferment 1920") mustBe true

  private def shouldNotContainExciseDefermentText(htmlText: String): Assertion =
    htmlText.contains("Excise deferment 1920") mustBe false

  trait Setup {
    val app: Application = application().build()

    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    implicit val msg: Messages        = messages(app)

    val startDate01: LocalDate = LocalDate.of(previousMonthDate.getYear, todaysDate.getMonth, DAY_01)
    val endDate01: LocalDate   = LocalDate.of(previousMonthDate.getYear, todaysDate.getMonth, DAY_26)

    val ddSttPeriod01: DutyDefermentStatementPeriod = DutyDefermentStatementPeriod(
      FileRole.DutyDefermentStatement,
      DDStatementType.ExciseDeferment,
      startDate01,
      startDate01,
      endDate01,
      Seq(ddSttFile08, ddSttFile09)
    )

    val ddSttPeriod02: DutyDefermentStatementPeriod = DutyDefermentStatementPeriod(
      FileRole.DutyDefermentStatement,
      DDStatementType.DutyDeferment,
      startDate01,
      startDate01,
      endDate01,
      Seq(ddSttFile10)
    )

    val ddSttPeriod03: DutyDefermentStatementPeriod = DutyDefermentStatementPeriod(
      FileRole.DutyDefermentStatement,
      DDStatementType.Supplementary,
      startDate01,
      startDate01,
      endDate01,
      Seq(ddSttFile02)
    )

    val ddSttPeriod04: DutyDefermentStatementPeriod = DutyDefermentStatementPeriod(
      FileRole.DutyDefermentStatement,
      DDStatementType.Weekly,
      startDate01,
      startDate01,
      endDate01,
      Seq(ddSttFile01)
    )

    val model01: DutyDefermentStatementPeriodsByMonth =
      DutyDefermentStatementPeriodsByMonth(todaysDate, Seq(ddSttPeriod01))

    val model02: DutyDefermentStatementPeriodsByMonth =
      DutyDefermentStatementPeriodsByMonth(todaysDate, Seq(ddSttPeriod02))

    val model03: DutyDefermentStatementPeriodsByMonth =
      DutyDefermentStatementPeriodsByMonth(todaysDate, Seq(ddSttPeriod03))

    val model04: DutyDefermentStatementPeriodsByMonth =
      DutyDefermentStatementPeriodsByMonth(
        todaysDate,
        Seq(ddSttPeriod02, ddSttPeriod04)
      )

    def view(model: DutyDefermentStatementPeriodsByMonth): Document =
      Jsoup.parse(duty_deferment_accordian_content.apply(model, 1).body)
  }
}