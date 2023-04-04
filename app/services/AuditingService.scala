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

import config.AppConfig
import models.responses.retrieve.ContactDetails
import models.{AuditModel, ChangeContactDetailsAuditEvent, ContactDetailsUserAnswers}
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.{Logger, LoggerLike}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Disabled, Failure, Success}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuditingService @Inject()(appConfig: AppConfig,
                                auditConnector: AuditConnector)(implicit executionContext: ExecutionContext) {

  val log: LoggerLike = Logger(this.getClass)

  val referrer: HeaderCarrier => String = _.headers(Seq(HeaderNames.REFERER)).headOption.fold("-")(_._2)

  def audit(auditModel: AuditModel)(implicit hc: HeaderCarrier): Future[AuditResult] = {
    val dataEvent = toExtendedDataEvent(appConfig.appName, auditModel, referrer(hc))

    auditConnector.sendExtendedEvent(dataEvent)
      .map { auditResult =>
        logAuditResult(auditResult)
        auditResult
      }
  }

  def changeContactDetailsAuditEvent(dan: String,
                                     previousContactDetails: ContactDetails,
                                     updatedContactDetails: ContactDetailsUserAnswers)
                                    (implicit hc: HeaderCarrier): Future[AuditResult] = {
    val event = ChangeContactDetailsAuditEvent(dan, previousContactDetails, updatedContactDetails)
    val auditModel = AuditModel("UpdateDefermentAccountCorrespondence", "Update contact details", Json.toJson(event))
    audit(auditModel)
  }

  private def toExtendedDataEvent(appName: String, auditModel: AuditModel, path: String)(implicit hc: HeaderCarrier): ExtendedDataEvent =
    ExtendedDataEvent(
      auditSource = appName,
      auditType = auditModel.auditType,
      tags = AuditExtensions.auditHeaderCarrier(hc).toAuditTags(auditModel.transactionName, path),
      detail = auditModel.detail
    )

  private def logAuditResult(auditResult: AuditResult): Unit =
    auditResult match {
      case Success =>
        log.debug("Splunk Audit Successful")
      case Failure(err, _) =>
        log.debug(s"Splunk Audit Error, message: $err")
      case Disabled =>
        log.debug(s"Auditing Disabled")
    }
}
