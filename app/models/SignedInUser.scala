package models
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}

case class SignedInUser(credentials: Option[Credentials],
                        name: Option[Name],
                        email: Option[String],
                        eori: String,
                        affinityGroup: Option[AffinityGroup],
                        internalId: Option[String],
                        enrolments: Enrolments,
                        allEoriHistory: Seq[EoriHistory])