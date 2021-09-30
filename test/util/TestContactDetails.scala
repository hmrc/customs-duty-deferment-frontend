/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package util

import models.responses.retrieve.{ContactDetails, ResponseCommon}
import models.{CDSAccountStatusId, DefermentAccountAvailable, UpdateContactDetailsResponse}
import org.mockito.Mockito.when
import org.mockito.scalatest.MockitoSugar
import services.CountriesProviderService
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}

trait TestContactDetails extends MockitoSugar {

  protected val encryptedParams = "DqKW7Ib0NIgZ8WCkxMdZyFqgqr9o2YP0o/eIy/oJ6yKhcV2Y5oEWQUQ="

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


  protected val validEori = "someEori"
  protected val validDan = "someDan"
  protected val validStatus: CDSAccountStatusId = DefermentAccountAvailable
  protected val danWithNoContactInformation = "someDanNoContactInformation"

  protected val successUpdateResponseCommon: ResponseCommon = ResponseCommon("OK", None, "2020-10-05T09:30:47Z", None)
  protected val successUpdateContactResponse: UpdateContactDetailsResponse = UpdateContactDetailsResponse(true)

  protected val failedUpdateResponseCommon: ResponseCommon = ResponseCommon("OK", Some("Error"), "2020-10-05T09:30:47Z", None)
  protected val failedUpdateContactResponse: UpdateContactDetailsResponse = UpdateContactDetailsResponse(false)

  protected val sessionId: SessionId = SessionId("session_1234")
  protected implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(sessionId))
  protected val fakeCountries = List()
  protected val mockCountriesProviderService: CountriesProviderService = mock[CountriesProviderService]
  when(mockCountriesProviderService.countries).thenReturn(fakeCountries)

}
