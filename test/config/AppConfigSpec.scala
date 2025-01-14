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

package config

import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import util.SpecBase

class AppConfigSpec extends SpecBase {
  "AppConfig" should {
    "contain correct values for the provided configuration" in new Setup {
      appConfig.appName mustBe "customs-duty-deferment-frontend"
      appConfig.subscribeCdsUrl mustBe "https://www.tax.service.gov.uk/customs-enrolment-services/cds/subscribe"
      appConfig.loginUrl mustBe "http://localhost:9553/bas-gateway/sign-in"
      appConfig.loginContinueUrl mustBe "http://localhost:9876/customs/payment-records"
      appConfig.financialsHomepage mustBe "http://localhost:9876/customs/payment-records"
      appConfig.yourContactDetailsUrl mustBe "http://localhost:9876/customs/payment-records/your-contact-details"
      appConfig.xClientIdHeader mustBe "c10ef6c6-8ffe-4a45-a159-d707ef90cf07"
      appConfig.signOutUrl mustBe "http://localhost:9553/bas-gateway/sign-out-without-state"
      appConfig.countriesFilename mustBe "location-autocomplete-canonical-list.json"
      appConfig.dutyDefermentContactDetailsEndpoint mustBe "/duty-deferment/contact-details"
      appConfig.dutyDefermentUpdateContactDetailsEndpoint mustBe "/duty-deferment/update-contact-details"
      appConfig.getAccountDetailsUrl mustBe
        "http://localhost:9878/customs-financials-api/duty-deferment/contact-details"
      appConfig.updateAccountAddressUrl mustBe
        "http://localhost:9878/customs-financials-api/duty-deferment/update-contact-details"
      appConfig.mongoSessionTtl mustBe 1200
      appConfig.mongoSessionContactDetailsTtl mustBe 7200
      appConfig.mongoAccountLinkTtl mustBe 7200
      appConfig.cdsEmailEnquiries mustBe "cdsf-enquiries@hmrc.gov.uk"
      appConfig.cdsEmailEnquiriesHref mustBe "mailto:cdsf-enquiries@hmrc.gov.uk"
      appConfig.chiefDDstatementsLink mustBe "https://secure.hmce.gov.uk/ecom/login/index.html"
      appConfig.ddAccountSupportLink mustBe
        "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/duty-deferment-scheme-general-enquiries"
      appConfig.timeout mustBe 900
      appConfig.countdown mustBe 120
      appConfig.feedbackService mustBe "https://www.development.tax.service.gov.uk/feedback/CDS-FIN"
      appConfig.customsFinancialsApi mustBe "http://localhost:9878/customs-financials-api"
      appConfig.customsSessionCacheUrl mustBe "http://localhost:9840/customs/session-cache"
      appConfig.customsDataStore mustBe "http://localhost:9893/customs-data-store"
      appConfig.sdesApi mustBe "http://localhost:9754/customs-financials-sdes-stub"
      appConfig.sddsUri mustBe "http://localhost:8323/customs-financials-sdds-stub/cds-homepage/cds/journey/start"
      appConfig.requestedStatementsUrl(linkId) mustBe
        s"http://localhost:9396/customs/historic-statement/requested/duty-deferment/$linkId"
      appConfig.historicRequestUrl(linkId) mustBe
        s"http://localhost:9396/customs/historic-statement/start-journey/duty-deferment/$linkId"
      appConfig.helpMakeGovUkBetterUrl mustBe
        "https://signup.take-part-in-research.service.gov.uk?" +
        "utm_campaign=CDSfinancials&utm_source=Other&utm_medium=other&t=HMRC&id=249"
      appConfig.contactFrontEndServiceId mustBe "CDS Financials"
    }

    "return correct value for deskProLinkUrlForServiceUnavailable" in new Setup {
      val path                                                     = "test_Path"
      implicit val reqHeaders: FakeRequest[AnyContentAsEmpty.type] = fakeRequest("GET", path)

      appConfig.deskProLinkUrlForServiceUnavailable mustBe
        "http://localhost:9250" +
        "/contact/report-technical-problem?newTab=true&amp;service=CDS%20FinancialsreferrerUrl=test_Path"
    }

    "emailFrontendService" should {
      "return correct url" in new Setup {
        appConfig.emailFrontendService mustBe "http://localhost:9898/manage-email-cds"
      }

      "return the correct value" in new Setup {
        appConfig.emailFrontendUrl mustBe "http://localhost:9898/manage-email-cds/service/customs-finance"
      }
    }
  }

  trait Setup {
    val linkId = "id"
  }
}
