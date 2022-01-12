/*
 * Copyright 2022 HM Revenue & Customs
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

import cache.UserAnswersCache
import config.{AppConfig, ErrorHandler}
import connectors.DataStoreConnector
import models.EoriHistory
import play.api.Application
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.{redirectLocation, running, status}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, Enrolment, EnrolmentIdentifier, Enrolments}
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.ExecutionContext.Implicits.global
import util.SpecBase

import scala.concurrent.Future

class DataActionSpec extends SpecBase {
  "transform" must {
    "return unauthorized when no sessionId present" in new Setup {

      when(mockErrorHandler.unauthorized()(any))
        .thenReturn(Html("unauthorized"))

      running(app) {
        val result = controller.onPageLoad()(FakeRequest())
        status(result) mustBe UNAUTHORIZED
        contentAsString(result).contains("unauthorized") mustBe true
      }
    }

    "redirect to session expired when no cached data returned" in new Setup {
      when(mockUserAnswersCache.retrieve(any)(any))
        .thenReturn(Future.successful(None))

      running(app) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith("/customs/duty-deferment/this-service-has-been-reset")
      }
    }

    "return ok when data is retrieved" in new Setup {
      when(mockUserAnswersCache.retrieve(any)(any))
        .thenReturn(Future.successful(Some(emptyUserAnswers)))

      running(app) {
        val result = controller.onPageLoad()(FakeRequest().withHeaders("X-Session-Id" -> "someSessionId"))
        status(result) mustBe OK
      }
    }
  }

  trait Setup {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockDataStoreConnector: DataStoreConnector = mock[DataStoreConnector]
    val mockErrorHandler: ErrorHandler = mock[ErrorHandler]
    val mockUserAnswersCache: UserAnswersCache = mock[UserAnswersCache]

    when(mockAuthConnector.authorise[Enrolments ~ Option[String]](any, any)(any, any))
      .thenReturn(Future.successful(Enrolments(Set(Enrolment("HMRC-CUS-ORG", Seq(EnrolmentIdentifier("EORINumber", "test")), "Active"))) ~ Some("internalId")))
    when(mockDataStoreConnector.getAllEoriHistory(any)(any))
      .thenReturn(Future.successful(Seq(EoriHistory("someEori", None, None))))

    val app: Application = application().overrides().build()
    val config: AppConfig = app.injector.instanceOf[AppConfig]
    val bodyParsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

    val authAction: AuthenticatedIdentifierAction = new AuthenticatedIdentifierAction(mockAuthConnector, config, bodyParsers, mockDataStoreConnector)
    val sessionIdAction: SessionIdAction = new SessionIdAction()(implicitly, mockErrorHandler)
    val dataRetrievalAction: DataRetrievalAction = new DataRetrievalActionImpl(mockUserAnswersCache)
    val dataRequiredAction: DataRequiredAction = new DataRequiredActionImpl()
    val controller = new Harness(authAction, sessionIdAction, dataRetrievalAction, dataRequiredAction)
  }

  implicit class Ops[A](a: A) {
    def ~[B](b: B): A ~ B = new ~(a, b)
  }

  class Harness(authAction: IdentifierAction,
                resolveSessionId: SessionIdAction,
                dataRetrievalAction: DataRetrievalAction,
                dataRequiredAction: DataRequiredAction
               ) {
    def onPageLoad(): Action[AnyContent] = (authAction andThen resolveSessionId andThen dataRetrievalAction andThen dataRequiredAction).async {
      implicit request => Future.successful(Results.Ok)
    }
  }

}
