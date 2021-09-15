package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {
  lazy val welshLanguageSupportEnabled: Boolean =
    config.get[Boolean]("features.welsh-language-support")

  lazy val registerCdsUrl: String = config.get[String]("external-urls.cdsRegisterUrl")
  lazy val subscribeCdsUrl: String = config.get[String]("external-urls.cdsSubscribeUrl")
  lazy val loginUrl: String = config.get[String]("external-urls.login")
  lazy val loginContinueUrl: String = config.get[String]("external-urls.loginContinue")
  lazy val financialsHomepage: String = config.get[String]("external-urls.customsFinancialsHomepage")


  lazy val customsFinancialsApi: String = servicesConfig.baseUrl("customs-financials-api") +
    config.get[String]("microservice.services.customs-financials-api.context")

  lazy val customsFinancialsSessionCacheUrl: String = servicesConfig.baseUrl("customs-financials-session-cache") +
    config.get[String]("microservice.services.customs-financials-session-cache.context")


}
