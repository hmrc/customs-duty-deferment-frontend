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

package services

import config.AppConfig
import org.scalatest.matchers.should.Matchers._
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase

import java.time.{LocalDate, LocalDateTime, LocalTime, OffsetDateTime}

class DateTimeServiceSpec extends SpecBase {

  "DateTimeService" should {
    "should return the current date and time in the LocalDateTime format" in new Setup {
      val now: LocalDateTime = dateTimeService.now()
      assert(now.isInstanceOf[LocalDateTime])
    }

    "getTimeStamp() should return the current date and time in the OffsetDateTime format" in new Setup {
      val timeStamp: OffsetDateTime = dateTimeService.getTimeStamp
      assert(timeStamp.isInstanceOf[OffsetDateTime])
    }

    "return the fixed date if fixedDateTime is enabled" in {

      val app = application().configure("features.fixed-systemdate-for-tests" -> true).build()
      val service = app.injector.instanceOf[DateTimeService]

      running(app) {
        service.systemDateTime() mustBe
          LocalDateTime.of(
            LocalDate.of(YEAR_2027, MONTH_12, DAY_20),
            LocalTime.of(HOUR_12, MINUTES_30)
          )
      }
    }
  }

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockConfig: AppConfig = mock[AppConfig]
    val dateTimeService = new DateTimeService(mockConfig)
  }
}
