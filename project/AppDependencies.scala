import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  private val hmrcMongoVersion = "2.1.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-conditional-form-mapping-play-30" % "2.0.0" cross CrossVersion.for3Use2_13//for3Use2_13With("", ".12")
      excludeAll (
      ExclusionRule("org.apache.pekko", "pekko-slf4j_2.13"),
      ExclusionRule("org.apache.pekko", "pekko-serialization-jackson_2.13"),
      ExclusionRule("org.apache.pekko", "pekko-actor-typed_2.13"),
      ExclusionRule("org.playframework", "play_2.13"),
      ExclusionRule("org.playframework.twirl", "twirl-api_2.13"),
      ExclusionRule("org.apache.pekko", "pekko-protobuf-v3_2.13"),
      ExclusionRule("org.playframework", "play-json_2.13"),
      ExclusionRule("org.apache.pekko", "pekko-actor_2.13"),
      ExclusionRule("org.apache.pekko", "pekko-stream_2.13"),
      ExclusionRule("org.scala-lang.modules", "scala-xml_2.13"),
      ExclusionRule("org.playframework", "play-streams_2.13"),
      ExclusionRule("org.playframework", "play-configuration_2.13"),
      ExclusionRule("org.playframework", "play-functional_2.13"),
      ExclusionRule("org.scala-lang.modules", "scala-parser-combinators_2.13"),
      ExclusionRule("com.typesafe", "ssl-config-core_2.13")
    ),
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
