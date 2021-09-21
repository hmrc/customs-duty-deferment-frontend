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

package util

import com.codahale.metrics.MetricRegistry
import com.kenshoo.play.metrics.Metrics
import controllers.actions.IdentifierAction
import models.DDStatementType.{Excise, Supplementary, Weekly}
import models.FileRole.DutyDefermentStatement
import models._
import org.mockito.scalatest.MockitoSugar
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import viewmodels.DutyDefermentStatementsForEori

trait SpecBase extends AnyWordSpecLike with Matchers with MockitoSugar with OptionValues {

  val dutyDefermentStatementFiles: Seq[DutyDefermentStatementFile] = List(
    DutyDefermentStatementFile(
      "someFilename",
      "downloadUrl",
      10L,
      DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", None)),
    DutyDefermentStatementFile(
      "someFilename2",
      "downloadUrl",
      10L,
      DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Pdf, DutyDefermentStatement, Supplementary, Some(true), Some("BACS"), "123456", None)),
    DutyDefermentStatementFile(
      "someFilename3",
      "downloadUrl",
      10L,
      DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv, DutyDefermentStatement, Excise, Some(false), Some("BACS"), "123456", None))
  )
  val dutyDefermentStatementMetadata1: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", "2018"),
    MetadataItem("PeriodStartMonth", "6"),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", "2018"),
    MetadataItem("PeriodEndMonth", "6"),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "CSV"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Weekly"),
    MetadataItem("DutyOverLimit", "Y"),
    MetadataItem("DutyPaymentType", "BACS"),
    MetadataItem("DAN", "123456")
  )

  val dutyDefermentStatementMetadata2: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", "2018"),
    MetadataItem("PeriodStartMonth", "6"),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", "2018"),
    MetadataItem("PeriodEndMonth", "6"),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "PDF"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Supplementary"),
    MetadataItem("DutyOverLimit", "Y"),
    MetadataItem("DutyPaymentType", "BACS"),
    MetadataItem("DAN", "123456")
  )

  val dutyDefermentStatementMetadata3: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", "2018"),
    MetadataItem("PeriodStartMonth", "6"),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", "2018"),
    MetadataItem("PeriodEndMonth", "6"),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "CSV"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Excise"),
    MetadataItem("DutyOverLimit", "N"),
    MetadataItem("DutyPaymentType", "BACS"),
    MetadataItem("DAN", "123456")
  )
  val eoriHistory: EoriHistory = EoriHistory("someEori", None, None)
  val accountLink: AccountLink = AccountLink("accountNumber", "linkId", AccountStatusOpen, Some(DefermentAccountAvailable))

  val dutyDefermentStatementsForEori: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
    eoriHistory,
    dutyDefermentStatementFiles,
    dutyDefermentStatementFiles
  )

  def application(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder().overrides(
      bind[IdentifierAction].to[FakeIdentifierAction],
      bind[Metrics].toInstance(new FakeMetrics)
    ).configure(
      "auditing.enabled" -> "false",
      "microservice.metrics.graphite.enabled" -> "false",
      "metrics.enabled" -> "false")
}

class FakeMetrics extends Metrics {
  override val defaultRegistry: MetricRegistry = new MetricRegistry
  override val toJson: String = "{}"
}
