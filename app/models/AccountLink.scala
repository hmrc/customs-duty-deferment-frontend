package models

import org.joda.time.DateTime
import play.api.libs.json.{Json, OFormat, Reads, Writes}

case class AccountLink(sessionId: String,
                       eori: String,
                       accountNumber: String,
                       accountStatus: CDSAccountStatus,
                       accountStatusId: Option[CDSAccountStatusId],
                       linkId: String,
                       lastUpdated: DateTime
                      ){

  def this(id: String, sessionCacheAccountLink: SessionCacheAccountLink) = {
    this(id,
      sessionCacheAccountLink.eori,
      sessionCacheAccountLink.accountNumber,
      sessionCacheAccountLink.accountStatus,
      sessionCacheAccountLink.accountStatusId,
      sessionCacheAccountLink.linkId,
      DateTime.now()
    )
  }
}

object AccountLink {
  implicit val lastUpdatedReads: Reads[DateTime] = uk.gov.hmrc.mongo.json.ReactiveMongoFormats.dateTimeRead
  implicit val lastUpdatedWrites: Writes[DateTime] = uk.gov.hmrc.mongo.json.ReactiveMongoFormats.dateTimeWrite
  implicit val accountLinkFormat: OFormat[AccountLink] = Json.format[AccountLink]
}