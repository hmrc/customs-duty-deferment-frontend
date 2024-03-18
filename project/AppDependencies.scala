import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.5.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % "9.0.0",
    "uk.gov.hmrc" %% "play-conditional-form-mapping-play-30" % "2.0.0",
    "org.typelevel" %% "cats-core" % "2.10.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % "1.7.0",
    "uk.gov.hmrc" %% "emailaddress-play-30" % "4.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test,
    "org.jsoup" % "jsoup" % "1.16.1",
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.8" % Test,
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.29" % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % "1.7.0"
  )
}
