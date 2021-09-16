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

package utils

import org.joda.time

import java.time.{LocalDate, ZoneId, ZoneOffset}
import java.util.Date

object DateConverters {
  implicit def toLocalDate(date: Date): LocalDate = date.toInstant.atZone(ZoneId.systemDefault()).toLocalDate

  implicit class OrderedLocalDate(date: LocalDate) extends Ordered[LocalDate] {
    def compare(that: LocalDate): Int = date.compareTo(that)
  }

  implicit def toJodaTime(date: LocalDate): time.LocalDate  = {
    new time.LocalDate(java.util.Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)))
  }
}
