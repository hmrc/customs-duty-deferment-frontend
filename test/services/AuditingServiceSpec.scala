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

import models.responses.retrieve.ContactDetails
import models.{AuditEori, AuditModel, ContactDetailsUserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.play.audit.http.connector._
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import util.SpecBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditingServiceSpec extends SpecBase {

  "AuditingService" should {

    "create the correct data event for a user requesting duty deferment statements" in new Setup {
      val model: AuditModel =
        AuditModel(AUDIT_TYPE, AUDIT_DUTY_DEFERMENT_TRANSACTION, Json.toJson(AuditEori(validEori, isHistoric = false)))
      await(auditingService.audit(model))

      val dataEventCaptor: ArgumentCaptor[ExtendedDataEvent] = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])
      verify(mockAuditConnector).sendExtendedEvent(dataEventCaptor.capture)(any, any)

      val dataEvent: ExtendedDataEvent = dataEventCaptor.getValue

      dataEvent.auditSource       should be(expectedAuditSource)
      dataEvent.auditType         should be(AUDIT_TYPE)
      dataEvent.detail.toString() should include(validEori)
      dataEvent.tags.toString()   should include(AUDIT_DUTY_DEFERMENT_TRANSACTION)
    }

    "create the correct data event for recording a successful audit event" in new Setup {
      val dataEventCaptor: ArgumentCaptor[ExtendedDataEvent] = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])
      await(auditingService.changeContactDetailsAuditEvent("dan", previousContactDetails, updatedContactDetails))

      verify(mockAuditConnector).sendExtendedEvent(dataEventCaptor.capture)(any, any)

      val dataEvent: ExtendedDataEvent = dataEventCaptor.getValue
      dataEvent.auditSource mustBe expectedAuditSource
      dataEvent.auditType mustBe "UpdateDefermentAccountCorrespondence"
      dataEvent.tags("transactionName") mustBe "Update contact details"
      dataEvent.detail.toString() must include(expectedPreviousContactDetails.toString)
      dataEvent.detail.toString() must include(expectedUpdatedContactDetails.toString)
    }

    "create the correct data event for logging a Failure audit event" in new Setup {

      when(mockAuditConnector.sendExtendedEvent(any)(any, any))
        .thenReturn(Future.successful(AuditResult.Failure("Auditing failed", None)))

      val dataEventCaptor: ArgumentCaptor[ExtendedDataEvent] = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])
      await(auditingService.changeContactDetailsAuditEvent("dan", previousContactDetails, updatedContactDetails))
      verify(mockAuditConnector).sendExtendedEvent(dataEventCaptor.capture)(any, any)

      val dataEvent: ExtendedDataEvent = dataEventCaptor.getValue
      dataEvent.auditSource mustBe expectedAuditSource
      dataEvent.auditType mustBe "UpdateDefermentAccountCorrespondence"
      dataEvent.tags("transactionName") mustBe "Update contact details"
      dataEvent.detail.toString() must include(expectedPreviousContactDetails.toString)
      dataEvent.detail.toString() must include(expectedUpdatedContactDetails.toString)
    }
  }

  trait Setup {
    val expectedAuditSource                        = "customs-duty-deferment-frontend"
    val AUDIT_DUTY_DEFERMENT_TRANSACTION           = "Display duty deferment statements"
    val AUDIT_VAT_CERTIFICATES_TRANSACTION         = "Display VAT certificates"
    val AUDIT_POSTPONED_VAT_STATEMENTS_TRANSACTION = "Display postponed VAT statements"
    val AUDIT_SECURITY_STATEMENTS_TRANSACTION      = "Display security statements"
    val AUDIT_TYPE                                 = "DisplayDutyDefermentStatements"

    when(mockConfig.appName).thenReturn("customs-duty-deferment-frontend")

    val previousContactDetails: ContactDetails = ContactDetails(
      contactName = Some("John Smith"),
      addressLine1 = "1 High Street",
      addressLine2 = Some("Town"),
      addressLine3 = Some("The County"),
      addressLine4 = Some("England"),
      postCode = Some("AB12 3CD"),
      countryCode = "0044",
      telephone = Some("1234567"),
      faxNumber = Some("7654321"),
      email = Some("abc@de.com")
    )

    val updatedContactDetails: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
      dan = "new dan",
      name = Some("John Smith"),
      addressLine1 = "2 Main Street",
      addressLine2 = Some("Town"),
      addressLine3 = Some("The County"),
      addressLine4 = Some("Highlands"),
      postCode = Some("SC12 3CD"),
      countryCode = "0045",
      countryName = Some("Scotland"),
      telephone = Some("1234567"),
      fax = Some("7654321"),
      email = Some("abc@de.com"),
      isNiAccount = false
    )

    val expectedPreviousContactDetails: JsValue = Json.parse("""{
        |"addressLine1":"1 High Street",
        |"postCode":"AB12 3CD",
        |"telephone":"1234567",
        |"faxNumber":"7654321",
        |"email":"abc@de.com",
        |"addressLine4":"England",
        |"addressLine3":"The County",
        |"contactName":"John Smith",
        |"countryCode":"0044",
        |"addressLine2":"Town"}""".stripMargin)

    val expectedUpdatedContactDetails: JsValue = Json.parse("""{
        |        "addressLine1":"2 Main Street",
        |        "postCode":"SC12 3CD",
        |        "telephone":"1234567",
        |        "faxNumber":"7654321",
        |        "email":"abc@de.com",
        |        "addressLine4":"Highlands",
        |        "addressLine3":"The County",
        |        "contactName":"John Smith",
        |        "countryCode":"0045",
        |"addressLine2":"Town"}""".stripMargin)

    val mockAuditConnector: AuditConnector = mock[AuditConnector]
    when(mockAuditConnector.sendExtendedEvent(any)(any, any)).thenReturn(Future.successful(AuditResult.Success))

    val auditingService = new AuditingService(mockConfig, mockAuditConnector)
  }

}
