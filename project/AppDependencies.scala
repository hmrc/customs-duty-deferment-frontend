import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "5.16.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "1.25.0-play-28",
    "uk.gov.hmrc" %% "play-conditional-form-mapping" % "1.10.0-play-28",
    "org.typelevel" %% "cats-core" % "2.3.0",
    "uk.gov.hmrc" %% "mongo-caching" % "7.0.0-play-28",
    "uk.gov.hmrc" %% "emailaddress" % "3.5.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.16.0" % Test,
    "org.jsoup" % "jsoup" % "1.13.1" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "org.mockito" %% "mockito-scala-scalatest" % "1.16.46" % "test, it"
  )
}
