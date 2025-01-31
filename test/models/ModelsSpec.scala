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

import play.api.libs.json.{
  IdxPathNode, JsArray, JsError, JsObject, JsPath, JsResult, JsString, JsSuccess, JsValue, Json, KeyPathNode,
  RecursiveSearch
}
import util.SpecBase

class ModelsSpec extends SpecBase {

  "RichJsObject" should {

    "set correct json value at specified location" in new Setup {

      val result01: JsResult[JsObject] = RichJsObject(jsonObj01).setObject(jpath01, jvalue01)
      result01 mustBe JsSuccess(Json.obj("key1" -> "value1"))

      val result02: JsResult[JsObject] = RichJsObject(jsonObj01).setObject(jpath03, jvalue03)
      result02 mustBe JsSuccess(jsonObj03)

      val result03: JsResult[JsObject] =
        RichJsObject(jsonObj03).setObject(JsPath(List(KeyPathNode("key2")) :+ IdxPathNode(0)), JsString("val2"))
      result03 mustBe JsSuccess(Json.parse(""" {"key1":"val1","key2":["val2"]} """))
    }

    "set json value at specifiec location should throw error" in new Setup {

      val result01: JsResult[JsObject] = RichJsObject(jsonObj01).setObject(jpath02, jvalue02)
      result01 mustBe a[JsError]

      val result02: JsResult[JsObject] = RichJsObject(jsonObj01).setObject(jpath04, jvalue04)
      result02 mustBe a[JsError]

      val result03: JsResult[JsValue] = RichJsValue(jsonObj04).set(jpath05, jvalue05)
      result03 mustBe a[JsError]

      val result04: JsResult[JsValue] = RichJsValue(jsonObj01).set(jpath05, jsonObj01)
      result04 mustBe a[JsError]

      val result05: JsResult[JsValue] = RichJsValue(jsonObj05).set(jpath06, jvalue05)
      result05 mustBe a[JsError]
    }

    "remove value at specified location" in new Setup {

      val result01: JsResult[JsObject] = RichJsObject(jsonObj02).removeObject(jpath01)
      result01 mustBe JsSuccess(Json.obj("key2" -> "val2"))

      val result02: JsResult[JsObject] = RichJsObject(jsonObj03).removeObject(jpath03)
      result02 mustBe JsSuccess(Json.obj("key1" -> "val1", "key2" -> Json.arr()))
    }

    "remove value at specified location should throw error" in new Setup {

      val result01: JsResult[JsObject] = RichJsObject(jsonObj02).removeObject(JsPath(List(KeyPathNode("key3"))))
      result01 mustBe a[JsError]

      val result02: JsResult[JsValue] = RichJsValue(jsonObj05).remove(JsPath \ 3)
      result02 mustBe a[JsError]

      val result03: JsResult[JsValue] = RichJsValue(jsonObj01).remove(jpath05)
      result03 mustBe a[JsError]

      val result04: JsResult[JsValue] = RichJsValue(jsonObj01).remove(JsPath(List.empty))
      result04 mustBe a[JsError]

      val result05: JsResult[JsValue] = RichJsValue(jsonObj05).remove(JsPath \ "key1")
      result05 mustBe a[JsError]
    }
  }

  trait Setup {
    val jsonObj01: JsObject = Json.obj("key1" -> "val1");
    val jsonObj02: JsObject = Json.obj("key1" -> "val1", "key2" -> "val2")
    val jsonObj03: JsObject = Json.obj("key1" -> "val1", "key2" -> Json.arr("val2"));
    val jsonObj04: JsObject = Json.obj("key1" -> "val1", "key2" -> Json.obj("nestedKey" -> "nestedValue"))
    val jsonObj05: JsArray  = Json.arr("Alpha", "Beta", "Gamma")
    val jsonObj06: JsObject = Json.obj("key1" -> Json.obj("key2" -> "value2"))

    val jpath01: JsPath   = JsPath(List(KeyPathNode("key1")))
    val jvalue01: JsValue = JsString("value1")

    val jpath02: JsPath   = JsPath(List(KeyPathNode("key2")) :+ RecursiveSearch("key1"))
    val jvalue02: JsValue = JsString("val2")

    val jpath03: JsPath   = JsPath(List(KeyPathNode("key2")) :+ IdxPathNode(0))
    val jvalue03: JsValue = JsString("val2")

    val jpath04: JsPath   = JsPath(List(KeyPathNode("key2")) :+ IdxPathNode(1))
    val jvalue04: JsValue = JsString("val2")

    val jpath05: JsPath   = JsPath \ "key1" \ 0
    val jvalue05: JsValue = Json.obj("newKey" -> "newValue")

    val jpath06: JsPath = JsPath \ "key1"

  }
}
