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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {

  lazy val appName: String = config.get[String]("appName")
  lazy val registerCdsUrl: String = config.get[String]("external-urls.cdsRegisterUrl")
  lazy val subscribeCdsUrl: String = config.get[String]("external-urls.cdsSubscribeUrl")
  lazy val loginUrl: String = config.get[String]("external-urls.login")
  lazy val loginContinueUrl: String = config.get[String]("external-urls.loginContinue")
  lazy val financialsHomepage: String = config.get[String]("external-urls.customsFinancialsHomepage")
  lazy val yourContactDetailsUrl: String = financialsHomepage + config.get[String]("external-urls.yourContactDetailsUrl")
  lazy val xClientIdHeader: String = config.get[String]("microservice.services.sdes.x-client-id")
  lazy val signOutUrl: String = config.get[String]("external-urls.signOut")
  lazy val countriesFilename: String = config.get[String]("countriesFilename")
  lazy val dutyDefermentContactDetailsEndpoint: String = config.get[String]("microservice.services.customs-financials-api.duty-deferment-contact-details-endpoint")
  lazy val dutyDefermentUpdateContactDetailsEndpoint: String = config.get[String]("microservice.services.customs-financials-api.duty-deferment-update-contact-details-endpoint")
  lazy val historicStatementsEnabled: Boolean = config.get[Boolean]("features.historic-statements-enabaled")
  lazy val getAccountDetailsUrl: String = customsFinancialsApi + dutyDefermentContactDetailsEndpoint
  lazy val updateAccountAddressUrl: String = customsFinancialsApi + dutyDefermentUpdateContactDetailsEndpoint
  val mongoSessionTtl: Int = config.get[Int]("mongodb.sessionTtl")
  val mongoSessionContactDetailsTtl: Int = config.get[Int]("mongodb.contactDetailsTtl")
  val mongoAccountLinkTtl: Int = config.get[Int]("mongodb.accountLinkTtl")

  lazy val cdsEmailEnquiries: String = config.get[String]("external-urls.cdsEmailEnquiries")
  lazy val cdsEmailEnquiriesHref: String = config.get[String]("external-urls.cdsEmailEnquiriesHref")
  lazy val chiefDDstatementsLink: String = config.get[String]("external-urls.chiefDDstatementsLink")
  lazy val ddAccountSupportLink: String = config.get[String]("external-urls.ddAccountSupportLink")

  lazy val timeout: Int = config.get[Int]("timeout.timeout")
  lazy val countdown: Int = config.get[Int]("timeout.countdown")

  lazy val feedbackService: String = config.get[String]("microservice.services.feedback.url") +
    config.get[String]("microservice.services.feedback.source")

  lazy val customsFinancialsApi: String = servicesConfig.baseUrl("customs-financials-api") +
    config.get[String]("microservice.services.customs-financials-api.context")

  lazy val customsSessionCacheUrl: String = servicesConfig.baseUrl("customs-financials-session-cache") +
    config.get[String]("microservice.services.customs-financials-session-cache.context")

  lazy val customsDataStore: String = servicesConfig.baseUrl("customs-data-store") +
    config.get[String]("microservice.services.customs-data-store.context")

  lazy val sdesApi: String = servicesConfig.baseUrl("sdes") +
    config.get[String]("microservice.services.sdes.context")

  lazy val sddsUri: String = servicesConfig.baseUrl("sdds") +
    config.get[String]("microservice.services.sdds.context") + "/cds-homepage/cds/journey/start"

  def requestedStatementsUrl(linkId: String): String =
    config.get[String]("external-urls.requestedStatements") + s"duty-deferment/$linkId"

  def historicRequestUrl(linkId: String): String =
    config.get[String]("external-urls.historicRequest") + s"duty-deferment/$linkId"

  lazy val helpMakeGovUkBetterUrl: String = config.get[String]("external-urls.helpMakeGovUkBetterUrl")
}
