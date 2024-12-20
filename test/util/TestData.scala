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

package util

import models.DDStatementType
import models.FileRole.DutyDefermentStatement
import models.responses.retrieve.{ContactDetails, ResponseCommon}
import models.*
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.when
import services.CountriesProviderService
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import viewmodels.DutyDefermentStatementsForEori

import java.time.LocalDate

trait TestData extends MockitoSugar {
  protected val encryptedParams                                            = "DqKW7Ib0NIgZ8WCkxMdZyFqgqr9o2YP0o/eIy/oJ6yKhcV2Y5oEWQUQ="
  protected val validEori                                                  = "someEori"
  protected val validDan                                                   = "someDan"
  protected val validStatus: CDSAccountStatusId                            = DefermentAccountAvailable
  protected val danWithNoContactInformation                                = "someDanNoContactInformation"
  protected val successUpdateResponseCommon: ResponseCommon                = ResponseCommon("OK", None, "2020-10-05T09:30:47Z", None)
  protected val successUpdateContactResponse: UpdateContactDetailsResponse = UpdateContactDetailsResponse(true)
  protected val failedUpdateResponseCommon: ResponseCommon                 =
    ResponseCommon("OK", Some("Error"), "2020-10-05T09:30:47Z", None)
  protected val failedUpdateContactResponse: UpdateContactDetailsResponse  = UpdateContactDetailsResponse(false)
  protected val sessionId: SessionId                                       = SessionId("session_1234")
  protected val fakeCountries: List[Country]                               = List()
  protected val emptyUserAnswers: UserAnswers                              = UserAnswers("someInternalId")
  protected val accNumber                                                  = "12345678"
  protected val testServiceUnavailableUrl                                  = "test_url"

  protected val validAccountContactDetails: ContactDetails = ContactDetails(
    Some("Mr First Name"),
    "Example Road",
    Some("Townsville"),
    Some("West County"),
    Some("London"),
    Some("AA00 0AA"),
    "GB",
    Some("+44444111111"),
    Some("+55555222222"),
    Some("example@email.com")
  )

  protected val mockCountriesProviderService: CountriesProviderService = mock[CountriesProviderService]
  when(mockCountriesProviderService.countries).thenReturn(fakeCountries)

  protected implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(sessionId))

  val contactDetailsUserAnswers: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
    validDan,
    Some("Example Name"),
    "Example Road",
    None,
    None,
    None,
    None,
    "GB",
    Some("United Kingdom"),
    Some("11111 222333"),
    None,
    Some("example@email.com"),
    isNiAccount = false
  )

  val editAddressDetailsUserAnswers: EditAddressDetailsUserAnswers = EditAddressDetailsUserAnswers(
    validDan,
    "Example Road",
    None,
    None,
    None,
    None,
    "GB",
    Some("United Kingdom"),
    isNiAccount = false
  )

  val editContactDetailsUserAnswers: EditContactDetailsUserAnswers = EditContactDetailsUserAnswers(
    validDan,
    Some("Example Name"),
    Some("11111 222333"),
    None,
    Some("example@email.com"),
    isNiAccount = false
  )

  val todaysDate: LocalDate         = LocalDate.now()
  val previousMonthDate: LocalDate  = todaysDate.minusMonths(1);
  val twoMonthsPriorDate: LocalDate = todaysDate.minusMonths(2);
  val periodStartDay: Int           = 1
  val periodEndDay: Int             = 8
  val fileSizeData: Long            = 10L
  val dan                           = "123456"
  val bacs                          = "BACS"

  def ddSttMetadata(
    startDate: LocalDate,
    startDay: Int,
    endDate: LocalDate,
    endDay: Int,
    fileFormat: FileFormat,
    ddSttType: DDStatementType,
    dutyOverLimit: Boolean
  ): DutyDefermentStatementFileMetadata =
    DutyDefermentStatementFileMetadata(
      startDate.getYear,
      startDate.getMonthValue,
      startDay,
      endDate.getYear,
      endDate.getMonthValue,
      endDay,
      fileFormat,
      DutyDefermentStatement,
      ddSttType,
      Some(dutyOverLimit),
      Some(bacs),
      dan,
      None
    )

  lazy val ddSttFile01: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Csv,
      DDStatementType.Weekly,
      true
    )
  )

  lazy val ddSttFile02: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename2",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Pdf,
      DDStatementType.Supplementary,
      true
    )
  )

  lazy val ddSttFile03: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename3",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Csv,
      DDStatementType.Excise,
      false
    )
  )

  lazy val ddSttFile04: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Csv,
      DDStatementType.Weekly,
      true
    )
  )

  lazy val ddSttFile05: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename4",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      twoMonthsPriorDate,
      periodStartDay,
      twoMonthsPriorDate,
      periodEndDay,
      FileFormat.Csv,
      DDStatementType.Weekly,
      false
    )
  )

  lazy val ddSttFile06: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename4",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      twoMonthsPriorDate,
      periodStartDay,
      twoMonthsPriorDate,
      periodEndDay,
      FileFormat.Pdf,
      DDStatementType.Excise,
      false
    )
  )

  lazy val ddSttFile07: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename2",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      twoMonthsPriorDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Pdf,
      DDStatementType.Weekly,
      true
    )
  )

  lazy val ddSttFile08: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Csv,
      DDStatementType.ExciseDeferment,
      true
    )
  )

  lazy val ddSttFile09: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Pdf,
      DDStatementType.ExciseDeferment,
      true
    )
  )

  lazy val ddSttFile10: DutyDefermentStatementFile = DutyDefermentStatementFile(
    "someFilename",
    "downloadUrl",
    fileSizeData,
    ddSttMetadata(
      previousMonthDate,
      periodStartDay,
      previousMonthDate,
      periodEndDay,
      FileFormat.Pdf,
      DDStatementType.DutyDeferment,
      true
    )
  )

  lazy val dutyDefermentStatementFiles: Seq[DutyDefermentStatementFile] = List(
    ddSttFile01,
    ddSttFile02,
    ddSttFile03,
    ddSttFile06
  )

  lazy val dutyDefermentStatementFiles02: Seq[DutyDefermentStatementFile] = List(ddSttFile05)

  lazy val dutyDefermentStatementFiles03: Seq[DutyDefermentStatementFile] = List(ddSttFile04, ddSttFile07)

  lazy val dutyDefermentStatementMetadata1: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", previousMonthDate.getYear.toString),
    MetadataItem("PeriodStartMonth", previousMonthDate.getMonthValue.toString),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", previousMonthDate.getYear.toString),
    MetadataItem("PeriodEndMonth", previousMonthDate.getMonthValue.toString),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "CSV"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Weekly"),
    MetadataItem("DutyOverLimit", "Y"),
    MetadataItem("DutyPaymentType", bacs),
    MetadataItem("DAN", dan)
  )

  lazy val dutyDefermentStatementMetadata2: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", previousMonthDate.getYear.toString),
    MetadataItem("PeriodStartMonth", previousMonthDate.getMonthValue.toString),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", previousMonthDate.getYear.toString),
    MetadataItem("PeriodEndMonth", previousMonthDate.getMonthValue.toString),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "PDF"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Supplementary"),
    MetadataItem("DutyOverLimit", "Y"),
    MetadataItem("DutyPaymentType", bacs),
    MetadataItem("DAN", dan)
  )

  lazy val dutyDefermentStatementMetadata3: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", previousMonthDate.getYear.toString),
    MetadataItem("PeriodStartMonth", previousMonthDate.getMonthValue.toString),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", previousMonthDate.getYear.toString),
    MetadataItem("PeriodEndMonth", previousMonthDate.getMonthValue.toString),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "CSV"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Excise"),
    MetadataItem("DutyOverLimit", "N"),
    MetadataItem("DutyPaymentType", bacs),
    MetadataItem("DAN", dan)
  )

  lazy val dutyDefermentStatementMetadata4: Seq[MetadataItem] = List(
    MetadataItem("PeriodStartYear", twoMonthsPriorDate.getYear.toString),
    MetadataItem("PeriodStartMonth", twoMonthsPriorDate.getMonthValue.toString),
    MetadataItem("PeriodStartDay", "1"),
    MetadataItem("PeriodEndYear", twoMonthsPriorDate.getYear.toString),
    MetadataItem("PeriodEndMonth", twoMonthsPriorDate.getMonthValue.toString),
    MetadataItem("PeriodEndDay", "8"),
    MetadataItem("FileType", "PDF"),
    MetadataItem("FileRole", "DutyDefermentStatement"),
    MetadataItem("DefermentStatementType", "Excise"),
    MetadataItem("DutyOverLimit", "N"),
    MetadataItem("DutyPaymentType", bacs),
    MetadataItem("DAN", dan)
  )

  lazy val eoriHistory: EoriHistory = EoriHistory("someEori", None, None)

  lazy val eoriHistory02: EoriHistory =
    EoriHistory("someEori", Some(twoMonthsPriorDate.withDayOfMonth(1)), Some(twoMonthsPriorDate.withDayOfMonth(DAY_28)))

  lazy val accountLink: AccountLink = AccountLink(
    "someEori",
    "accountNumber",
    "linkId",
    AccountStatusOpen,
    Some(DefermentAccountAvailable),
    isNiAccount = false
  )

  lazy val dutyDefermentStatementsForEori01: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
    eoriHistory,
    dutyDefermentStatementFiles,
    dutyDefermentStatementFiles,
    todaysDate
  )

  lazy val dutyDefermentStatementsForEori02: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
    eoriHistory02,
    dutyDefermentStatementFiles02,
    dutyDefermentStatementFiles02,
    todaysDate
  )

  lazy val dutyDefermentStatementsForEori03: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
    eoriHistory,
    dutyDefermentStatementFiles,
    dutyDefermentStatementFiles03,
    todaysDate
  )

  lazy val dutyDefermentAccountLink: DutyDefermentAccountLink = DutyDefermentAccountLink(
    eori = "someEori",
    dan = validDan,
    linkId = "someLinkId",
    status = AccountStatusOpen,
    statusId = validStatus,
    isNiAccount = false
  )

  protected val YEAR_2027  = 2027
  protected val MONTH_12   = 12
  protected val DAY_01     = 1
  protected val DAY_02     = 2
  protected val DAY_20     = 20
  protected val DAY_25     = 25
  protected val DAY_26     = 26
  protected val DAY_28     = 28
  protected val HOUR_12    = 12
  protected val MINUTES_30 = 30
}
