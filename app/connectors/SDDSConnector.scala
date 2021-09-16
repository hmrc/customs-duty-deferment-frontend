package connectors

import config.AppConfig
import models.{SDDSRequest, SDDSResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SDDSConnector @Inject()(httpClient: HttpClient, appConfig: AppConfig)(implicit executionContext: ExecutionContext) {
  def startJourney(returnUrl: String, backUrl: String, dan: String, email: String)(implicit hc: HeaderCarrier): Future[String] =
    httpClient.POST[SDDSRequest, SDDSResponse](
      appConfig.sddsUri,
      SDDSRequest(returnUrl, backUrl, dan, email)
    ).map(_.nextUrl)
}
