/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.Inject
import config.AppConfig
import models.Country
import play.api.libs.json.{JsArray, JsString, Json}
import play.api.{Environment, Logger}

import javax.inject.Singleton


@Singleton
class CountriesProviderService @Inject()(env: Environment, appConfig: AppConfig) {

  private val log = Logger(this.getClass)

  private def countryCode: String => String = _.split(":")(1).trim

  private val countriesFilename = appConfig.countriesFilename

  val countries: List[Country] = {
    def fromJsonFile: List[Country] =
      Json.parse(env.classLoader.getResourceAsStream(countriesFilename)) match {
        case JsArray(cs) =>
          cs.toList.collect {
            case JsArray(Seq(c: JsString, cc: JsString)) =>
              Country(c.value, countryCode(cc.value))
          }
        case _ =>
          log.error("Could not read JSON array of countries from : " + countriesFilename)
          throw new IllegalArgumentException("Could not read JSON array of countries from : " + countriesFilename)
      }

    fromJsonFile.sortBy(_.countryName)
  }

  private lazy val countriesNamesSet: Set[String] = countries.map(_.countryName).toSet
  def isValidCountryName(countryName: String): Boolean = countriesNamesSet.contains(countryName)

  private lazy val countriesNamesMap: Map[String, String] = countries
    .map { country => country.countryCode -> country.countryName }
    .toMap

  def getCountryName(countryCode: String): Option[String] = countriesNamesMap.get(countryCode)
}
