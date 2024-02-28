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

import play.api.libs.json.{IdxPathNode, JsError, JsObject, JsPath, JsResult, JsString, JsSuccess, JsValue, Json, KeyPathNode, RecursiveSearch}
import util.SpecBase

class ModelsSpec extends SpecBase {

  "RichJsObject" should {

    "set correct json value at specified location" in new Setup {

      val result01: JsResult[JsObject] = jsonTestData01.setObject(jpath01, jvalue01)
      result01 mustBe JsSuccess(Json.obj("key1" -> "value1"))

      val result02: JsResult[JsObject] = jsonTestData01.setObject(jpath02, jvalue02)
      result02 mustBe a[JsError]

      val result03: JsResult[JsObject] = jsonTestData01.setObject(jpath03, jvalue03)
      result03 mustBe JsSuccess(Json.obj("key1" -> "val1", "key2" -> Json.arr("val2")))

      val result04: JsResult[JsObject] = jsonTestData01.setObject(jpath04, jvalue04)
      result04 mustBe a[JsError]

      val result05: JsResult[JsObject] = jsonTestData03.setObject(
        JsPath(List(KeyPathNode("key2")) :+ IdxPathNode(0)),
        JsString("val2"))
      result05 mustBe JsSuccess(Json.parse(""" {"key1":"val1","key2":["val2"]} """))

    }

    "remove value at specified location" in new Setup {

      val result01: JsResult[JsObject] = jsonTestData02.removeObject(jpath01)
      result01 mustBe JsSuccess(Json.obj("key2" -> "val2"))

      val result02: JsResult[JsObject] = jsonTestData02.removeObject(JsPath(List(KeyPathNode("key3"))))
      result02 mustBe a[JsError]

      val result03: JsResult[JsObject] = jsonTestData03.removeObject(jpath03)
      result03 mustBe JsSuccess(Json.obj("key1" -> "val1", "key2" -> Json.arr()))
    }
  }

  trait Setup {
    val jsonTestData01: RichJsObject = RichJsObject(Json.obj("key1" -> "val1"))
    val jsonTestData02: RichJsObject = RichJsObject(Json.obj("key1" -> "val1", "key2" -> "val2"))
    val jsonTestData03: RichJsObject = RichJsObject(Json.obj("key1" -> "val1", "key2" -> Json.arr("val2")))

    val jpath01: JsPath = JsPath(List(KeyPathNode("key1")))
    val jvalue01: JsValue = JsString("value1")

    val jpath02: JsPath = JsPath(List(KeyPathNode("key2")) :+ RecursiveSearch("key1"))
    val jvalue02: JsValue = JsString("val2")

    val jpath03: JsPath = JsPath(List(KeyPathNode("key2")) :+ IdxPathNode(0))
    val jvalue03: JsValue = JsString("val2")

    val jpath04: JsPath = JsPath(List(KeyPathNode("key2")) :+ IdxPathNode(1))
    val jvalue04: JsValue = JsString("val2")

  }
}
