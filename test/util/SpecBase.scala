package util

import com.codahale.metrics.MetricRegistry
import com.kenshoo.play.metrics.Metrics
import controllers.actions.IdentifierAction
import models.DDStatementType.Weekly
import models.FileRole.DutyDefermentStatement
import models.{DutyDefermentStatementFile, DutyDefermentStatementFileMetadata, EoriHistory, FileFormat, MetadataItem}
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
      DutyDefermentStatementFileMetadata(2018, 6, 1, 2018, 6, 8, FileFormat.Csv, DutyDefermentStatement, Weekly, Some(true), Some("BACS"), "123456", None))
  )
  val dutyDefermentStatementMetadata: Seq[MetadataItem] = List(
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
  val eoriHistory: EoriHistory = EoriHistory("someEori", None, None)

  val dutyDefermentStatementsForEori: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
    eoriHistory,
    dutyDefermentStatementFiles,
    dutyDefermentStatementFiles
  )

  def application(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder().overrides(
      bind[IdentifierAction].to[FakeIdentifierAction],
      bind[Metrics].toInstance(new FakeMetrics)
    ).configure("auditing.enabled" -> "false")
}

class FakeMetrics extends Metrics {
  override val defaultRegistry: MetricRegistry = new MetricRegistry
  override val toJson: String = "{}"
}
