import scoverage.ScoverageKeys

val appName         = "customs-duty-deferment-frontend"
val testDirectory   = "test"
val scala3_3_6      = "3.3.6"
val silencerVersion = "1.7.14"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala3_3_6

lazy val scalastyleSettings = Seq(
  scalastyleConfig := baseDirectory.value / "scalastyle-config.xml",
  (Test / scalastyleConfig) := baseDirectory.value / testDirectory / "test-scalastyle-config.xml"
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(
    PlayKeys.playDefaultPort := 9397,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all")),
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
    Test / scalacOptions ++= Seq(
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"
    ),
    ScoverageKeys.coverageExcludedFiles := List("<empty>", "Reverse.*", ".*(BuildInfo|Routes|testOnly).*", ".*views.*")
      .mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Assets / pipelineStages := Seq(gzip),
    libraryDependencies ++= Seq(
      compilerPlugin(
        "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.for3Use2_13With("", ".12")
      ),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.for3Use2_13With("", ".12")
    )
  )
  .settings(scalastyleSettings)
  .settings(
    scalafmtDetailedError := true,
    scalafmtPrintDiff := true,
    scalafmtFailOnErrors := true
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    libraryDependencies ++= Seq("uk.gov.hmrc" %% "bootstrap-test-play-30" % AppDependencies.bootstrapVersion % Test)
  )

addCommandAlias(
  "runAllChecks",
  ";clean;compile;coverage;test;it/test;scalafmtCheckAll;scalastyle;Test/scalastyle;coverageReport"
)
