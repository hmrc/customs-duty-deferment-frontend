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
import config.AppConfig
import connectors.DataStoreConnector
import controllers.routes
import models.EoriHistory
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import util.SpecBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends SpecBase {

  class Harness(authAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Results.Ok }
  }

  implicit class Ops[A](a: A) {
    def ~[B](b: B): A ~ B = new ~(a, b)
  }

  "Auth Action" when {

    "redirect the user to unauthorised controller when has no enrolments" in {
      val mockAuthConnector = mock[AuthConnector]
      val mockDataStoreConnector = mock[DataStoreConnector]

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(Future.successful(Enrolments(Set.empty) ~ Some("internalId")))

      val app = application().overrides().build()
      val config = app.injector.instanceOf[AppConfig]
      val bodyParsers = app.injector.instanceOf[BodyParsers.Default]

      val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, config, bodyParsers, mockDataStoreConnector)
      val controller = new Harness(authAction)

      running(app) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "redirect the user to unauthorised controller when has no eori enrolment" in {
      val mockAuthConnector = mock[AuthConnector]
      val mockDataStoreConnector = mock[DataStoreConnector]

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(Future.successful(Enrolments(Set(Enrolment("someKey", Seq(EnrolmentIdentifier("someKey", "someValue")), "ACTIVE"))) ~ Some("internalId")))

      val app = application().overrides().build()
      val config = app.injector.instanceOf[AppConfig]
      val bodyParsers = app.injector.instanceOf[BodyParsers.Default]

      val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, config, bodyParsers, mockDataStoreConnector)
      val controller = new Harness(authAction)

      running(app) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "redirect the user to unauthorised controller when an auth error happens" in {
      val mockAuthConnector = mock[AuthConnector]
      val mockDataStoreConnector = mock[DataStoreConnector]

      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(Future.failed(new RuntimeException("something went wrong")))

      val app = application().overrides().build()
      val config = app.injector.instanceOf[AppConfig]
      val bodyParsers = app.injector.instanceOf[BodyParsers.Default]

      val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, config, bodyParsers, mockDataStoreConnector)
      val controller = new Harness(authAction)

      running(app) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/not-subscribed-for-cds")
      }
    }

    "continue journey on successful response from auth" in {
      val mockAuthConnector = mock[AuthConnector]
      val mockDataStoreConnector = mock[DataStoreConnector]

      when(mockDataStoreConnector.getAllEoriHistory(any)(any))
        .thenReturn(Future.successful(Seq(EoriHistory("someEori", None, None))))


      when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
        .thenReturn(Future.successful(
          Enrolments(Set(Enrolment("HMRC-CUS-ORG", Seq(EnrolmentIdentifier("EORINumber", "test")), "Active"))) ~ Some("internalId")
        )
        )

      val app = application().overrides().build()
      val config = app.injector.instanceOf[AppConfig]
      val bodyParsers = app.injector.instanceOf[BodyParsers.Default]

      val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, config, bodyParsers, mockDataStoreConnector)
      val controller = new Harness(authAction)

      running(app) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe OK
      }
    }

    "the user hasn't logged in" must {

      "redirect the user to log in " in {

        val app = application().build()
        val mockDataStoreConnector = mock[DataStoreConnector]


        val bodyParsers = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[AppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), frontendAppConfig, bodyParsers, mockDataStoreConnector)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user's session has expired" must {

      "redirect the user to log in " in {

        val app = application().build()
        val mockDataStoreConnector = mock[DataStoreConnector]


        val bodyParsers = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[AppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new BearerTokenExpired), frontendAppConfig, bodyParsers, mockDataStoreConnector)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" must {

      "redirect the user to the unauthorised page" in {

        val app = application().build()
        val mockDataStoreConnector = mock[DataStoreConnector]


        val bodyParsers = app.injector.instanceOf[BodyParsers.Default]
        val frontendAppConfig = app.injector.instanceOf[AppConfig]

        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new InsufficientEnrolments), frontendAppConfig, bodyParsers, mockDataStoreConnector)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())

        status(result) mustBe SEE_OTHER

        redirectLocation(result).value mustBe routes.NotSubscribedController.onPageLoad.url
      }
    }
  }
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
