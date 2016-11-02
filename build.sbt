lazy val sparkVersion = "2.0.1"

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

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core",
  "org.apache.spark" %% "spark-mllib",
  "org.apache.spark" %% "spark-sql"
).map(_ % sparkVersion % "provided")

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
  .settings(moduleName := "spark-ml")
  .settings(buildSettings)
  .aggregate(chapter7)

lazy val chapter2 = project
  .settings(moduleName := "chapter2")
  .settings(buildSettings)

lazy val chapter7 = project
  .settings(moduleName := "chapter7")
  .settings(buildSettings)
