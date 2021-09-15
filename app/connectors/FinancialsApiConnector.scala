package connectors

import config.AppConfig
import models.FileRole
import play.mvc.Http.Status
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FinancialsApiConnector @Inject()(http: HttpClient,
                                       appConfig: AppConfig)(implicit executionContext: ExecutionContext) {
  def deleteNotification(eori: String, fileRole: FileRole)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val apiEndpoint = appConfig.customsFinancialsApi + s"/eori/$eori/notifications/$fileRole"
    http.DELETE[HttpResponse](apiEndpoint).map(_.status == Status.OK)
  }
}
