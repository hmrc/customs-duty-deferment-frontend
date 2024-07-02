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

import com.codahale.metrics.MetricRegistry
import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.UserAnswers
import org.jsoup.nodes.Document
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{Assertion, OptionValues}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.play.bootstrap.metrics.Metrics
import scala.jdk.CollectionConverters._


trait SpecBase extends AnyWordSpecLike
  with Matchers
  with MockitoSugar
  with OptionValues
  with TestData {

  implicit class DocumentHelper(document: Document) {
    def containsLink(link: String): Boolean = {
      val results = document.getElementsByTag("a").asScala.toList
      results.exists(_.attr("href") == link)
    }

    def containsLinkWithText(link: String, text: String): Boolean = {
      val results = document.getElementsByTag("a").asScala.toList
      val foundLinks = results.filter(_.attr("href") == link)
      if (foundLinks.nonEmpty) foundLinks.exists(_.text == text) else false
    }

    def containsElementById(id: String): Assertion = {
      assert(document.getElementsByAttribute("id").asScala.toList.exists(_.id() == id))
    }

    def notContainElementById(id: String): Assertion = {
      assert(!document.getElementsByAttribute("id").asScala.toList.exists(_.id() == id))
    }
  }

  def fakeRequest(method: String, path: String): FakeRequest[AnyContentAsEmpty.type] = {
    FakeRequest(method, path)
      .withHeaders("X-Session-ID" -> "someSessionId")
  }

  def fakeRequestWithCsrf(method: String, path: String): FakeRequest[AnyContentAsEmpty.type] = {
    fakeRequest(method, path)
      .withCSRFToken
      .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]
  }

  def application(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder().overrides(
      bind[IdentifierAction].to[FakeIdentifierAction],
      bind[DataRetrievalAction].to(new FakeDataRetrievalAction(userAnswers)),
      bind[Metrics].toInstance(new FakeMetrics)
    ).configure(
      "play.filters.csp.nonce.enabled" -> false,
      "auditing.enabled" -> "false",
      "microservice.metrics.graphite.enabled" -> "false",
      "metrics.enabled" -> "false")

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(
    fakeRequest("", "")
  )
}

class FakeMetrics extends Metrics {
  override val defaultRegistry: MetricRegistry = new MetricRegistry
}

object TestImplicits {
  implicit class RemoveCsrf(s: String) {
    def removeCsrf(): String = {
      val regEx = "<[/]?input type[^>]*>"
      s.replaceAll(regEx, "")
    }
  }
}
