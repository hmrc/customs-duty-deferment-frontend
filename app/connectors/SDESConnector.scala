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
import models.FileFormat.{SdesFileFormats, filterFileFormats}
import models._
import play.api.libs.json.Json
import services.AuditingService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.http.HttpReads.Implicits._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SDESConnector @Inject()(http: HttpClient,
                              auditingService: AuditingService,
                              appConfig: AppConfig)(implicit executionContext: ExecutionContext) {

  val AUDIT_TYPE = "DisplayDutyDefermentStatements"
  val AUDIT_DUTY_DEFERMENT_TRANSACTION = "Display duty deferment statements"

  def getDutyDefermentStatements(eori: String, dan: String)(implicit hc: HeaderCarrier): Future[Seq[DutyDefermentStatementFile]] = {
    val sdesDutyDefermentStatementListUrl: String = appConfig.sdesApi + "/files-available/list/DutyDefermentStatement"
    auditingService.audit(AuditModel(AUDIT_TYPE, AUDIT_DUTY_DEFERMENT_TRANSACTION, Json.toJson(AuditEori(eori, isHistoric = false))))
    http.GET[Seq[FileInformation]](
      sdesDutyDefermentStatementListUrl,
      headers = Seq("x-client-id" -> appConfig.xClientIdHeader, "X-SDES-Key" -> s"$eori-$dan")
    )(implicitly, HeaderCarrier(), implicitly)
      .map(_.map(_.toDutyDefermentStatementFile))
      .map(filterFileFormats(SdesFileFormats))
  }
}
