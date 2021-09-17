package connectors

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import util.SpecBase

class SessionCacheConnectorSpec extends SpecBase {

  "retrieveSession" should {
    "return an account link on a successful response" in new Setup {
      pending
    }

    "return None on a failed response" in new Setup {
      pending
    }
  }

  "removeSession" should {
    "return the HttpResponse returned from the API" in new Setup {
      pending
    }
  }

  trait Setup {
    val app: Application = GuiceApplicationBuilder().build()
  }
}
