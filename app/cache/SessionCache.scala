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

package cache

import play.api.libs.json._
import uk.gov.hmrc.cache.model.Cache
import uk.gov.hmrc.cache.repository.CacheMongoRepository

import uk.gov.hmrc.mongo.cache.CacheItem
import uk.gov.hmrc.mongo.cache.MongoCacheRepository
import uk.gov.hmrc.mongo.cache.EntityCache

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
trait SessionCache[A] {
  //this: MongoCacheRepository =>
  this: EntityCache

  val key: String
  implicit val ec: ExecutionContext

  def store(id: String, a: A)(implicit writes: Writes[A]): Future[Boolean] = {
    createOrUpdate(id, key, Json.toJson(a)) map (_ => true)
  }

  def remove(id: String): Future[Boolean] = {
    removeById(id).map(_.ok)
  }

  def retrieve(id: String)(implicit reads: Reads[A]): Future[Option[A]] = {
    findById(id).map {
      case Some(CacheItem(_, Some(data), _, _)) =>
        (data \ key).toOption.flatMap { keyData =>
          Json.fromJson[A](keyData) match {
            case jsSuccess: JsSuccess[A] => Some(jsSuccess.value)
            case _: JsError => None
          }
        }
      case _ => None
    }
  }
}