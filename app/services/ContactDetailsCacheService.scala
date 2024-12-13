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

package services

import cache.ContactDetailsCache
import connectors.CustomsFinancialsApiConnector
import models.responses.retrieve.ContactDetails
import models.{ContactDetailsUserAnswers, DataRequest}
import play.api.Logger
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class ContactDetailsCacheService @Inject() (
  connector: CustomsFinancialsApiConnector,
  contactDetailsCache: ContactDetailsCache
)(implicit ec: ExecutionContext) {

  val log: Logger = Logger(this.getClass)

  def getContactDetails(internalId: String, dan: String, eori: String)(implicit
    hc: HeaderCarrier
  ): Future[ContactDetails] =
    getAndCacheContactDetails(internalId: String, dan: String, eori: String)

  private def getAndCacheContactDetails(identifier: String, dan: String, eori: String)(implicit
    hc: HeaderCarrier
  ): Future[ContactDetails] = {
    val idWithDan: String = s"$identifier$dan"
    contactDetailsCache.retrieve(idWithDan).flatMap {
      case Some(value) =>
        Future.successful(value)
      case None        =>
        for {
          contactDetails <- connector.getContactDetails(dan, eori)
          _              <- contactDetailsCache.store(idWithDan, contactDetails)
        } yield contactDetails
    }
  }

  def updateContactDetails(
    contactDetailsUserAnswers: ContactDetailsUserAnswers
  )(implicit request: DataRequest[AnyContent]): Future[Boolean] = {
    val idWithDan: String              = s"${request.identifier}${contactDetailsUserAnswers.dan}"
    val contactDetails: ContactDetails = contactDetailsUserAnswers.withWhitespaceTrimmed.toContactDetails
    contactDetailsCache.store(idWithDan, contactDetails)
  }
}
