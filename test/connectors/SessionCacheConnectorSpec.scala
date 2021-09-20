package connectors

import models.{AccountLink, AccountStatusOpen}
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}
import util.SpecBase

import scala.concurrent.Future

class SessionCacheConnectorSpec extends SpecBase {

  "retrieveSession" should {
    "return an account link on a successful response" in new Setup {
      val link: AccountLink = AccountLink("12345", "someId", AccountStatusOpen, None)

      when[Future[AccountLink]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(link))

      running(app) {
        val result = await(connector.retrieveSession("someId", "someLink"))
        result mustBe Some(link)
      }
    }

    "return None on a failed response" in new Setup {
      when[Future[AccountLink]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.failed(UpstreamErrorResponse("Not Found", 404, 404)))

      running(app){
        val result = await(connector.retrieveSession("someId", "someLink"))
        result mustBe None
      }
    }
  }

  "removeSession" should {
    "return the HttpResponse returned from the API" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse(Status.OK, "")))

      running(app) {
        val result = await(connector.removeSession("someId"))
        result mustBe true
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = GuiceApplicationBuilder().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: SessionCacheConnector =
      app.injector.instanceOf[SessionCacheConnector]
  }
}
