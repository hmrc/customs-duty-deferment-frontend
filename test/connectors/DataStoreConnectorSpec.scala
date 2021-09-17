package connectors

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import util.SpecBase

class DataStoreConnectorSpec extends SpecBase {

  "getAllEoriHistory" should {
    "return a sequence of eori history when the request is successful" in new Setup {
      pending
    }

    "return an empty sequence of EORI when the request fails" in new Setup {
      pending
    }
  }

  "getEmail" should {
    "return an email address when the request is successful and undeliverable is not present in the response" in new Setup {
      pending
    }

    "return no email address when the request is successful and undeliverable is present in the response" in new Setup {
      pending
    }

    "return no email when a NOT_FOUND response is returned" in new Setup {
      pending
    }
  }

  trait Setup {
    val app: Application = GuiceApplicationBuilder().build()
  }
}
