/*
 * Copyright 2021 HM Revenue & Customs
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
import models._
import models.responses.retrieve.ContactDetails
import services.AuditingService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsFinancialsApiConnector @Inject()(appConfig: AppConfig,
                                              httpClient: HttpClient,
                                              auditingService: AuditingService)(implicit ec: ExecutionContext) {

  def getContactDetails(dan: String, eori: String)(implicit hc: HeaderCarrier): Future[ContactDetails] = {
    val request = GetContactDetailsRequest(dan, eori)
    httpClient.POST[GetContactDetailsRequest, ContactDetails](appConfig.getAccountDetailsUrl, request)
  }

  def updateContactDetails(dan: String, eori: String, oldContactDetails: ContactDetails, newContactDetails: ContactDetailsUserAnswers)
                          (implicit hc: HeaderCarrier): Future[UpdateContactDetailsResponse] = {
    val trimmed: ContactDetailsUserAnswers = newContactDetails.withWhitespaceTrimmed
    val request: UpdateContactDetailsRequest = UpdateContactDetailsRequest(dan, eori, trimmed)
    val response = httpClient.POST[UpdateContactDetailsRequest, UpdateContactDetailsResponse](appConfig.updateAccountAddressUrl, request)
    auditingService.changeContactDetailsAuditEvent(dan, oldContactDetails, trimmed)
    response
  }
}
