package connectors

import models.DDStatementType.Weekly
import models.FileRole.DutyDefermentStatement
import models._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.{Application, inject}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import util.SpecBase

import scala.concurrent.Future

class SDESConnectorSpec extends SpecBase {

  "getDutyDefermentStatements" should {
    "return a sequence of duty deferment files on a successful response" in new Setup {
      val fileInformation: Seq[FileInformation] =
        Seq(FileInformation("someFilename", "downloadUrl", 10L, Metadata(dutyDefermentStatementMetadata)))

      when[Future[Seq[FileInformation]]](mockHttpClient.GET(any, any, any)(any, any, any))
        .thenReturn(Future.successful(fileInformation))

      running(app) {
        val result = await(connector.getDutyDefermentStatements("someEori", "someDan"))
        result mustBe dutyDefermentStatementFiles
      }
    }
  }

  trait Setup {
    val mockHttpClient: HttpClient = mock[HttpClient]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val app: Application = GuiceApplicationBuilder().overrides(
      inject.bind[HttpClient].toInstance(mockHttpClient)
    ).build()

    val connector: SDESConnector = app.injector.instanceOf[SDESConnector]
  }
}
