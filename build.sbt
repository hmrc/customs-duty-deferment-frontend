import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.targetJvm

val appName = "customs-duty-deferment-frontend"
val testDirectory = "test"
val bootstrap = "7.22.0"
val scala2_13_8 = "2.13.8"
val silencerVersion = "1.17.13"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala2_13_8

lazy val scalastyleSettings = Seq(
  scalastyleConfig := baseDirectory.value / "scalastyle-config.xml",
  (Test / scalastyleConfig) := baseDirectory.value / testDirectory / "test-scalastyle-config.xml")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := scala2_13_8,
    targetJvm := "jvm-11",
    PlayKeys.playDefaultPort := 9397,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= Seq(
      "-P:silencer:pathFilters=routes",
      "-P:silencer:pathFilters=target/.*",
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:patvars",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"),
    Test / scalacOptions ++= Seq(
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:patvars",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"),
    ScoverageKeys.coverageExcludedFiles := List(
      "<empty>", "Reverse.*", ".*(BuildInfo|Routes|testOnly).*", ".*views.*").mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Assets / pipelineStages := Seq(gzip),
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
  )
  .configs(IntegrationTest)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(scalastyleSettings)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(libraryDependencies ++= Seq("uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrap % Test))
