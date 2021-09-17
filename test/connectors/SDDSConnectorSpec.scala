package connectors

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import util.SpecBase

class SDDSConnectorSpec extends SpecBase {

  "startJourney" should {
    "return a redirect URL on a successful response" in new Setup {
      pending
    }
  }

  trait Setup {
    val app: Application = GuiceApplicationBuilder().build()
  }
}
