import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.targetJvm

val appName = "customs-duty-deferment-frontend"
val testDirectory = "test"
val bootstrap = "9.0.0"
val scala3_3_3 = "3.3.3"
val silencerVersion = "1.7.14"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala3_3_3

lazy val scalastyleSettings = Seq(
  scalastyleConfig := baseDirectory.value / "scalastyle-config.xml",
  (Test / scalastyleConfig) := baseDirectory.value / testDirectory / "test-scalastyle-config.xml")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    targetJvm := "jvm-11",
    PlayKeys.playDefaultPort := 9397,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
  /*  scalacOptions ++= Seq(
      //"-P:silencer:pathFilters=routes",
      //"-P:silencer:pathFilters=target/.*",
      "-no-indent",
      "-Wunused:imports",
      "-Wunused:params",
      //"-Wunused:patvars",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"),*/
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all")),
    Test / scalacOptions ++= Seq(
      "-Wunused:imports",
      "-Wunused:params",
      //"-Wunused:patvars",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"),

    ScoverageKeys.coverageExcludedFiles := List(
      "<empty>", "Reverse.*", ".*(BuildInfo|Routes|testOnly).*", ".*views.*").mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageMinimumBranchTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Assets / pipelineStages := Seq(gzip),
    libraryDependencies ++= Seq(
      compilerPlugin(
        "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.for3Use2_13With("", ".12")),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.for3Use2_13With("",".12")
    )
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(scalastyleSettings)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(libraryDependencies ++= Seq("uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrap % Test))

addCommandAlias("runAllChecks", ";clean;compile;coverage;test;it/test;scalastyle;Test/scalastyle;coverageReport")
