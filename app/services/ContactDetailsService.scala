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

  val config: Config = ConfigFactory.parseMap(
    Map(
      appConfig.contactDetailsCryptoBaseConfigKey + ".key" -> appConfig.contactDetailsCryptoEncryptionKey
    ).asJava
  )

  def getEncyptedDanWithStatus(dan: String, statusId: Int): Future[String] = {
    val danWithStatus = s"$dan|$statusId"
    val crypto = new CryptoGCMWithKeysFromConfig(appConfig.contactDetailsCryptoBaseConfigKey, config)
    val encryptedDanWithStatus = encode(crypto.encrypt(PlainText(danWithStatus)).value, "UTF8")
    Future(appConfig.contactDetailsUri + encryptedDanWithStatus)
  }
}