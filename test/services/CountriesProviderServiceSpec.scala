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
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import play.api.Environment
import util.SpecBase

import java.io.ByteArrayInputStream

class CountriesProviderServiceSpec extends SpecBase {

  "CountriesProviderService" should {

    "return list of Countries for correct json data" in new Setup {
      when(mockEnv.classLoader.getResourceAsStream(anyString))
        .thenReturn(new ByteArrayInputStream(testData02.getBytes()))

      val countryService: CountriesProviderService = new CountriesProviderService(mockEnv, mockConfig)
      countryService.countries.size mustBe 2
    }

    "throws error if unable to read json file" in new Setup {
      when(mockEnv.classLoader.getResourceAsStream(anyString))
        .thenReturn(new ByteArrayInputStream(testData01.getBytes()))

      intercept[IllegalArgumentException] {
        new CountriesProviderService(mockEnv, mockConfig)
      }
    }
  }

  trait Setup {
    val testData01 = "{}"
    val testData02 = "[[\"United Kingdom\",\"country:GB\"],[\"Ireland\",\"country:IE\"]]"
    val fileName   = "countries.json"

    val mockEnv: Environment         = mock[Environment]
    val mockConfig: AppConfig        = mock[AppConfig]
    val mockClassLoader: ClassLoader = mock[ClassLoader]

    when(mockConfig.countriesFilename).thenReturn(fileName)
    when(mockEnv.classLoader).thenReturn(mockClassLoader)
  }
}
