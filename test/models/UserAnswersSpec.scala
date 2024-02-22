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

import scala.util.{Failure, Success, Try}

class UserAnswersSpec extends SpecBase {

  "UserAnswers" should {

    "get a value correctly" in new Setup {
      val gettable: MockGettable[String] = MockGettable[String](JsPath(List(KeyPathNode("key"))))
      val result: Option[String] = userAnswers.get(gettable)(Reads.StringReads)
      result mustBe Some("value")
    }
    "get None if value is incorrect" in new Setup {
      val gettable: MockGettable[String] = MockGettable[String](JsPath(List(KeyPathNode("key2"))))
      val result: Option[String] = userAnswers.get(gettable)(Reads.StringReads)
      result mustBe None
    }

    "set a value correctly" in new Setup {
      val settable: MockSettable[String] = MockSettable[String](
        JsPath(List(KeyPathNode("key"))),
        (_, ua) => Success(ua)
      )
      val updatedAnswers: Try[UserAnswers] =
        userAnswers.set(settable, "new value")(Writes.StringWrites)
      updatedAnswers mustBe a[Success[_]]
      updatedAnswers.get.data mustBe Json.obj("key" -> "new value")
    }

    "get a failure if wrong key is used in seting a value" in new Setup {
      val settable: MockSettable[String] = MockSettable[String](
        JsPath(Nil),
        (_, ua) => Success(ua)
      )
      assertThrows[JsResultException] {
        val updatedAnswers =
          userAnswers.set(settable, "new value")(Writes.StringWrites)
        updatedAnswers.get.data mustBe Json.obj("key" -> "new value")
      }
    }

    "remove a value correctly" in new Setup {
      val settable: MockSettable[String] = MockSettable[String](
        JsPath(List(KeyPathNode("key"))),
        (_, ua) => Success(ua)
      )
      val updatedAnswers: Try[UserAnswers] = userAnswers.remove(settable)
      updatedAnswers mustBe a[Success[_]]
      updatedAnswers.get.data mustBe Json.obj()
    }

    "should not to remove if key is not found" in new Setup {
      val settable: MockSettable[String] = MockSettable[String](
        JsPath(List(KeyPathNode("key2"))),
        (_, ua) => Success(ua)
      )
      val updatedAnswers: Try[UserAnswers] = userAnswers.remove(settable)
      updatedAnswers mustBe a[Success[_]]
      updatedAnswers.get.data mustBe Json.obj("key"->"value")
    }

    "implicits datatimeformat should not be null " in new Setup {
      assert(MongoJavatimeFormats.Implicits.jatLocalDateTimeFormat != null)
    }

  }

  trait Setup {

    val sampleId = "sampleId"
    val sampleData: JsObject = Json.obj("key" -> "value")
    val sampleLocalDateTime: LocalDateTime = LocalDateTime.now
    val userAnswers: UserAnswers = UserAnswers(sampleId, sampleData, sampleLocalDateTime)

    case class MockGettable[A](path: JsPath) extends Gettable[A]
    case class MockSettable[A](
        path: JsPath,
        cleanup: (Option[A], UserAnswers) => Try[UserAnswers]
    ) extends Settable[A]

  }
}
