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

import cats.implicits.toFunctorOps
import play.api.libs.json._
import uk.gov.hmrc.mongo.cache.{DataKey, MongoCacheRepository}

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
trait SessionCache[A] {
  this: MongoCacheRepository[String] =>

  val key: String
  implicit val ec: ExecutionContext

  def store(id: String, a: A)(implicit writes: Writes[A]): Future[Boolean] =
    put(id)(DataKey(key), a).as(true)

  def remove(id: String): Future[Boolean] =
    delete(id)(DataKey(key)).as(true)

  def retrieve(id: String)(implicit reads: Reads[A]): Future[Option[A]] =
    get[A](id)(DataKey(key))
}