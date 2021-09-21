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

package services

import config.AppConfig
import models.{AuditEori, AuditModel}
import org.mockito.captor.{ArgCaptor, Captor}
import org.scalatest.matchers.should.Matchers._
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector._
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import util.SpecBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditingServiceSpec extends SpecBase {

  "AuditingService" should {

    "create the correct data event for a user requesting duty deferment statements" in new Setup {
      val model: AuditModel = AuditModel(AUDIT_TYPE, AUDIT_DUTY_DEFERMENT_TRANSACTION, Json.toJson(AuditEori(eori, isHistoric = false)))
      await(auditingService.audit(model))

      val dataEventCaptor: Captor[ExtendedDataEvent] = ArgCaptor[ExtendedDataEvent]
      verify(mockAuditConnector).sendExtendedEvent(dataEventCaptor.capture)(any, any)
      val dataEvent: ExtendedDataEvent = dataEventCaptor.value

      dataEvent.auditSource should be(expectedAuditSource)
      dataEvent.auditType should be(AUDIT_TYPE)
      dataEvent.detail.toString() should include(eori)
      dataEvent.tags.toString() should include(AUDIT_DUTY_DEFERMENT_TRANSACTION)
    }
  }

  trait Setup {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val expectedAuditSource = "customs-duty-deferment-frontend"
    val eori = "EORI"
    val AUDIT_DUTY_DEFERMENT_TRANSACTION = "DUTYDEFERMENTSTATEMENTS"
    val AUDIT_VAT_CERTIFICATES_TRANSACTION = "Display VAT certificates"
    val AUDIT_POSTPONED_VAT_STATEMENTS_TRANSACTION = "Display postponed VAT statements"
    val AUDIT_SECURITY_STATEMENTS_TRANSACTION = "Display security statements"
    val AUDIT_TYPE = "SDESCALL"

    val mockConfig: AppConfig = mock[AppConfig]
    when(mockConfig.appName).thenReturn("customs-duty-deferment-frontend")

    val mockAuditConnector: AuditConnector = mock[AuditConnector]
    when(mockAuditConnector.sendExtendedEvent(any)(any, any)).thenReturn(Future.successful(AuditResult.Success))

    val auditingService = new AuditingService(mockConfig, mockAuditConnector)
  }

}