package connectors

import models.SDDSResponse
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import util.SpecBase

import scala.concurrent.Future

class SDDSConnectorSpec extends SpecBase {

  "startJourney" should {
    "return a redirect URL on a successful response" in new Setup {
      val response: SDDSResponse = SDDSResponse("someUrl")

      when[Future[SDDSResponse]](mockHttpClient.POST(any, any, any)(any, any, any, any))
        .thenReturn(Future.successful(response))

      running(app){
        val result = await(connector.startJourney("dan", "some@email.com"))
        result mustBe "someUrl"
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = GuiceApplicationBuilder().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: SDDSConnector = app.injector.instanceOf[SDDSConnector]
  }
}
