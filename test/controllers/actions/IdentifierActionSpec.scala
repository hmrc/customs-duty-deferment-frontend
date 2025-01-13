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

package controllers.actions

import com.google.inject.Inject
import connectors.DataStoreConnector
import controllers.routes
import models.EoriHistory
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction(_ => Results.Ok)
  }

  implicit class Ops[A](a: A) {
    def ~[B](b: B): A ~ B = new ~(a, b)
  }

  "Auth Action" when {
    "redirect the user to unauthorised controller when has no enrolments" in new Setup {

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("internalId")))

      val authAction =
        new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers, mockDataStoreConnector)

      val controller = new Harness(authAction)

      running(application()) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "redirect the user to unauthorised controller when has no eori enrolment" in new Setup {

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(
          Future.successful(
            Enrolments(Set(Enrolment("someKey", Seq(EnrolmentIdentifier("someKey", "someValue")), "ACTIVE"))) ~ Some(
              "internalId"
            )
          )
        )

      val authAction =
        new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers, mockDataStoreConnector)

      val controller = new Harness(authAction)

      running(application()) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "redirect the user to unauthorised controller when an auth error happens" in new Setup {

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(Future.failed(new RuntimeException("something went wrong")))

      val authAction =
        new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers, mockDataStoreConnector)

      val controller = new Harness(authAction)

      running(application()) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "redirect the user to unauthorised controller when InternalID is empty" in new Setup {

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(
          Future.successful(
            Enrolments(Set(Enrolment("HMRC-CUS-ORG", Seq(EnrolmentIdentifier("EORINumber", "test")), "Active"))) ~ None
          )
        )

      val authAction =
        new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers, mockDataStoreConnector)

      val controller = new Harness(authAction)

      running(application()) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "continue journey on successful response from auth" in new Setup {

      when(mockDataStoreConnector.getAllEoriHistory(any)(any))
        .thenReturn(Future.successful(Seq(EoriHistory("someEori", None, None))))

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(
          Future.successful(
            Enrolments(Set(Enrolment("HMRC-CUS-ORG", Seq(EnrolmentIdentifier("EORINumber", "test")), "Active"))) ~ Some(
              "internalId"
            )
          )
        )

      val authAction =
        new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers, mockDataStoreConnector)

      val controller = new Harness(authAction)

      running(application()) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))

        status(result) mustBe OK
      }
    }

    "the user hasn't logged in" must {
      "redirect the user to log in " in new Setup {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new MissingBearerToken),
          appConfig,
          bodyParsers,
          mockDataStoreConnector
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(appConfig.loginUrl)
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in new Setup {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new BearerTokenExpired),
          appConfig,
          bodyParsers,
          mockDataStoreConnector
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(appConfig.loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in new Setup {

        val authAction = new AuthenticatedIdentifierAction(
          new FakeFailingAuthConnector(new InsufficientEnrolments),
          appConfig,
          bodyParsers,
          mockDataStoreConnector
        )
        val controller = new Harness(authAction)
        val result     = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustBe routes.NotSubscribedController.onPageLoad.url
      }
    }
  }

  trait Setup {
    val mockAuthConnector: AuthConnector           = mock[AuthConnector]
    val mockDataStoreConnector: DataStoreConnector = mock[DataStoreConnector]
    val bodyParsers: BodyParsers.Default           = application().injector.instanceOf[BodyParsers.Default]
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[A] =
    Future.failed(exceptionToReturn)
}
