/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import config.AppConfig
import models.responses.retrieve.ContactDetails
import models.{
  ContactDetailsUserAnswers, FileRole, GetContactDetailsRequest, UpdateContactDetailsRequest,
  UpdateContactDetailsResponse
}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.mvc.Http.Status
import services.AuditingService
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsFinancialsApiConnector @Inject() (
  appConfig: AppConfig,
  httpClient: HttpClientV2,
  auditingService: AuditingService
)(implicit ec: ExecutionContext) {

  def deleteNotification(fileRole: FileRole)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val endPointUrl = s"${appConfig.customsFinancialsApi}/eori/notifications/$fileRole"

    httpClient
      .delete(url"$endPointUrl")
      .execute[HttpResponse]
      .flatMap { response =>
        Future.successful(response.status == Status.OK)
      }
      .recover { case _ => false }
  }

  def getContactDetails(dan: String, eori: String)(implicit hc: HeaderCarrier): Future[ContactDetails] = {
    val request = GetContactDetailsRequest(dan, eori)

    httpClient
      .post(url"${appConfig.getAccountDetailsUrl}")
      .withBody[GetContactDetailsRequest](request)
      .execute[ContactDetails]
      .flatMap { response =>
        Future.successful(response)
      }
  }

  def updateContactDetails(
    dan: String,
    eori: String,
    oldContactDetails: ContactDetails,
    newContactDetails: ContactDetailsUserAnswers
  )(implicit hc: HeaderCarrier): Future[UpdateContactDetailsResponse] = {
    val trimmed: ContactDetailsUserAnswers   = newContactDetails.withWhitespaceTrimmed
    val request: UpdateContactDetailsRequest = UpdateContactDetailsRequest(dan, eori, trimmed)

    val response = httpClient
      .post(url"${appConfig.updateAccountAddressUrl}")
      .withBody[UpdateContactDetailsRequest](request)
      .execute[UpdateContactDetailsResponse]
      .flatMap { response =>
        Future.successful(response)
      }

    auditingService.changeContactDetailsAuditEvent(dan, oldContactDetails, trimmed)

    response
  }
}
