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

import util.SpecBase
import views.html.components.country_field
import mappings.EditAddressDetailsFormProvider
import models.EditAddressDetailsUserAnswers
import play.api.data.Form
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import services.CountriesProviderService
import play.api.Application

class CountryFieldSpec extends SpecBase {

  "component" should {

    "display the contents correctly" in new Setup {
      countryFieldComponent.body.contains("Mozambique") mustBe true
      countryFieldComponent.body.contains("Russia") mustBe true
      countryFieldComponent.body.contains("Poland") mustBe true
      countryFieldComponent.body.contains("United Kingdom") mustBe true
      countryFieldComponent.body.contains("India") mustBe true

      countryFieldComponent.body.contains(messages("country-picker.hint.screen-reader")) mustBe true
      countryFieldComponent.body.contains(messages("countryCode")) mustBe true
    }
  }

  trait Setup {
    val app: Application                                      = applicationBuilder().build()
    implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/some/resource/path")

    val validForm: Form[EditAddressDetailsUserAnswers] = new EditAddressDetailsFormProvider().apply()
    val countries: CountriesProviderService            = app.injector.instanceOf[CountriesProviderService]

    val countryFieldComponent: HtmlFormat.Appendable =
      app.injector
        .instanceOf[country_field]
        .apply(validForm, "countryCode", messages("country-picker.country.label"), countries.countries, None)
  }
}
