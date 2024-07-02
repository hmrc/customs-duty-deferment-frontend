import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  private val hmrcMongoVersion = "2.1.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-conditional-form-mapping-play-30" % "2.0.0" cross CrossVersion.for3Use2_13With("", ".12")
  /*    excludeAll (
      ExclusionRule("org.apache.pekko", "pekko-slf4j_2.13")
    )*/,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30" % "10.3.0",
    "org.typelevel" %% "cats-core" % "2.10.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % hmrcMongoVersion,
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test,
    "org.jsoup" % "jsoup" % "1.17.2",
    "org.scalatestplus" %% "mockito-4-11" %"3.2.18.0" % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % hmrcMongoVersion
  )
}
