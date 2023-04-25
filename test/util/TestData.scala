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

import models.DDStatementType.{Excise, Supplementary, Weekly}
import models.FileRole.DutyDefermentStatement
import models.responses.retrieve.{ContactDetails, ResponseCommon}
import models.{AccountLink, AccountStatusOpen, CDSAccountStatusId, ContactDetailsUserAnswers, DefermentAccountAvailable, DutyDefermentAccountLink, DutyDefermentStatementFile, DutyDefermentStatementFileMetadata, EditAddressDetailsUserAnswers, EditContactDetailsUserAnswers, EoriHistory, FileFormat, MetadataItem, UpdateContactDetailsResponse, UserAnswers}
import org.mockito.scalatest.MockitoSugar
import services.CountriesProviderService
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import viewmodels.DutyDefermentStatementsForEori

trait TestData extends MockitoSugar {
  protected val encryptedParams = "DqKW7Ib0NIgZ8WCkxMdZyFqgqr9o2YP0o/eIy/oJ6yKhcV2Y5oEWQUQ="
  protected val validEori = "someEori"
  protected val validDan = "someDan"
  protected val validStatus: CDSAccountStatusId = DefermentAccountAvailable
  protected val danWithNoContactInformation = "someDanNoContactInformation"
  protected val successUpdateResponseCommon: ResponseCommon = ResponseCommon("OK", None, "2020-10-05T09:30:47Z", None)
  protected val successUpdateContactResponse: UpdateContactDetailsResponse = UpdateContactDetailsResponse(true)
  protected val failedUpdateResponseCommon: ResponseCommon = ResponseCommon("OK", Some("Error"), "2020-10-05T09:30:47Z", None)
  protected val failedUpdateContactResponse: UpdateContactDetailsResponse = UpdateContactDetailsResponse(false)
  protected val sessionId: SessionId = SessionId("session_1234")
  protected val fakeCountries = List()
  protected val emptyUserAnswers: UserAnswers = UserAnswers("someInternalId")

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
    Some("example@email.com")
  )

  val editAddressDetailsUserAnswers: EditAddressDetailsUserAnswers = EditAddressDetailsUserAnswers(
    validDan,
    "Example Road",
    None,
    None,
    None,
    None,
    "GB",
    Some("United Kingdom")
  )

  val editContactDetailsUserAnswers: EditContactDetailsUserAnswers = EditContactDetailsUserAnswers(
    validDan,
    Some("Example Name"),
    Some("11111 222333"),
    None,
    Some("example@email.com")
  )

  lazy val dutyDefermentStatementFiles: Seq[DutyDefermentStatementFile] = List(
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
  lazy val dutyDefermentStatementMetadata1: Seq[MetadataItem] = List(
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
  lazy val dutyDefermentStatementMetadata2: Seq[MetadataItem] = List(
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
  lazy val dutyDefermentStatementMetadata3: Seq[MetadataItem] = List(
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
  lazy val eoriHistory: EoriHistory = EoriHistory("someEori", None, None)
  lazy val accountLink: AccountLink = AccountLink(
    "someEori", "accountNumber", "linkId", AccountStatusOpen, Some(DefermentAccountAvailable))

  lazy val dutyDefermentStatementsForEori: DutyDefermentStatementsForEori = DutyDefermentStatementsForEori(
    eoriHistory,
    dutyDefermentStatementFiles,
    dutyDefermentStatementFiles
  )

  lazy val dutyDefermentAccountLink: DutyDefermentAccountLink = DutyDefermentAccountLink(
    eori = "someEori",
    dan = validDan,
    linkId = "someLinkId",
    status = AccountStatusOpen,
    statusId = validStatus
  )
}
