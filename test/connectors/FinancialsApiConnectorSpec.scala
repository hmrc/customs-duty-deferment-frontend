package connectors

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import util.SpecBase

class FinancialsApiConnectorSpec extends SpecBase {

  "deleteNotification" should {
    "return true when the response from the API returns OK" in new Setup {
      pending
    }

    "return false when the response from the API is not OK" in new Setup {
      pending
    }
  }

  trait Setup {
    val app: Application = GuiceApplicationBuilder().build()
  }
}
