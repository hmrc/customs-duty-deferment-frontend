package connectors

import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import util.SpecBase

class SdesConnectorSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "return a sequence of duty deferment files on a successful response" in new Setup {
      pending
    }
  }

  trait Setup {
    val app: Application = GuiceApplicationBuilder().build()
  }
}
