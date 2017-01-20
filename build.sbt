lazy val sparkVersion = "2.1.0"

lazy val buildSettings = Seq(
  organization := "io.github.benfradet",
  version := "1.0",
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core",
    "org.apache.spark" %% "spark-mllib",
    "org.apache.spark" %% "spark-sql"
  ).map(_ % sparkVersion % "provided"),
  scalacOptions ++= compilerOptions
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint"
)

lazy val ml = project.in(file("."))
  .settings(moduleName := "spark-ml-in-action")
  .settings(buildSettings)
  .aggregate(chapter2, chapter4, chapter7)

lazy val chapter2 = project
  .settings(moduleName := "chapter2")
  .settings(buildSettings)

lazy val chapter4 = project
  .settings(moduleName := "chapter4")
  .settings(buildSettings)

lazy val chapter7 = project
  .settings(moduleName := "chapter7")
  .settings(buildSettings)
