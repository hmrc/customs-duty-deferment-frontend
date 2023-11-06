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

package models

import java.time.LocalDateTime
import util.SpecBase
import play.api.libs.json._
import queries.{Gettable, Settable}
import scala.util.{Success, Try}
import models.UserAnswers

class UserAnswersSpec extends SpecBase {

  "UserAnswers" should {

    "get a value correctly" in new Setup {
      val gettable = MockGettable[String](JsPath(List(KeyPathNode("key"))))
      val result = userAnswers.get(gettable)(Reads.StringReads)
      result mustBe Some("value")
    }

    "set a value correctly" in new Setup {
      val settable = MockSettable[String](
        JsPath(List(KeyPathNode("key"))),
        (_, ua) => Success(ua)
      )
      val updatedAnswers =
        userAnswers.set(settable, "new value")(Writes.StringWrites)
      updatedAnswers mustBe a[Success[_]]
      updatedAnswers.get.data mustBe Json.obj("key" -> "new value")
    }

    "remove a value correctly" in new Setup {
      val settable = MockSettable[String](
        JsPath(List(KeyPathNode("key"))),
        (_, ua) => Success(ua)
      )
      val updatedAnswers = userAnswers.remove(settable)
      updatedAnswers mustBe a[Success[_]]
      updatedAnswers.get.data mustBe Json.obj()
    }
  }

  trait Setup {

    val sampleId = "sampleId"
    val sampleData = Json.obj("key" -> "value")
    val sampleLocalDateTime = LocalDateTime.now
    val userAnswers = UserAnswers(sampleId, sampleData, sampleLocalDateTime)

    case class MockGettable[A](path: JsPath) extends Gettable[A]
    case class MockSettable[A](
        path: JsPath,
        cleanup: (Option[A], UserAnswers) => Try[UserAnswers]
    ) extends Settable[A]

  }
}
