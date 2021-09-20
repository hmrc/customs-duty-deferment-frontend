package connectors

import models.FileRole.DutyDefermentStatement
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import util.SpecBase

import scala.concurrent.Future

class FinancialsApiConnectorSpec extends SpecBase {

  "deleteNotification" should {
    "return true when the response from the API returns OK" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse.apply(Status.OK, "")))

      running(app){
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe true
      }
    }

    "return false when the response from the API is not OK" in new Setup {
      when(mockHttpClient.DELETE[HttpResponse](any, any)(any, any, any))
        .thenReturn(Future.successful(HttpResponse.apply(Status.NOT_FOUND, "")))

      running(app){
        val result = await(connector.deleteNotification("someEori", DutyDefermentStatement))
        result mustBe false
      }
    }
  }

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockHttpClient: HttpClient = mock[HttpClient]

    val app: Application = GuiceApplicationBuilder().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: FinancialsApiConnector =
      app.injector.instanceOf[FinancialsApiConnector]
  }
}
