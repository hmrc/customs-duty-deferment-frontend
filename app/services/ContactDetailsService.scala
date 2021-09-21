/*
 * Copyright 2021 HM Revenue & Customs
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

package services

import java.net.URLEncoder.encode
import com.typesafe.config.{Config, ConfigFactory}
import config.AppConfig

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.crypto.{CryptoGCMWithKeysFromConfig, PlainText}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContactDetailsService @Inject() (implicit appConfig: AppConfig, executionContext: ExecutionContext){

  val configKey: String = appConfig.contactDetailsCryptoBaseConfigKey
  val encryptionKey: String = appConfig.contactDetailsCryptoEncryptionKey
  val config: Config = ConfigFactory.parseMap(Map(configKey + ".key" -> encryptionKey).asJava)

  def getEncyptedDanWithStatus(dan: String, statusId: Int): Future[String] = {
    val danWithStatus = s"$dan|$statusId"
    val crypto = new CryptoGCMWithKeysFromConfig(configKey, config)
    val encryptedDanWithStatus = encode(crypto.encrypt(PlainText(danWithStatus)).value, "UTF8")
    Future(appConfig.contactDetailsUri + encryptedDanWithStatus)
  }
}