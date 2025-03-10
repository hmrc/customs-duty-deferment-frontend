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

import connectors.SDESConnector
import models.{DutyDefermentStatementFile, EoriHistory}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.DutyDefermentStatementsForEori

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DocumentService @Inject() (sdesConnector: SDESConnector, dateTimeService: DateTimeService)(implicit
  ec: ExecutionContext
) {

  def getDutyDefermentStatements(eoriHistory: EoriHistory, dan: String)(implicit
    hc: HeaderCarrier
  ): Future[DutyDefermentStatementsForEori] =
    sdesConnector
      .getDutyDefermentStatements(eoriHistory.eori, dan)
      .map(auditFiles(_))
      .map(_.partition(_.metadata.statementRequestId.isEmpty))
      .map { case (current, requested) =>
        DutyDefermentStatementsForEori(eoriHistory, current, requested, dateTimeService.systemDateTime().toLocalDate)
      }

  private def auditFiles(files: Seq[DutyDefermentStatementFile]): Seq[DutyDefermentStatementFile] =
    files.map { file =>
      file
    }
}
