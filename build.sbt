ThisBuild / scalaVersion     := scala212
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.github.fehu"
ThisBuild / crossScalaVersions := scala212 :: scala211 :: Nil

lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.8"


lazy val root = (project in file("."))
  .settings(
    name := "opentracing-scala-akka",
    libraryDependencies ++= Seq(
      "io.opentracing" % "opentracing-util" % opentracingVersion,
      "io.opentracing" % "opentracing-mock" % opentracingVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "io.opentracing" % "opentracing-util" % opentracingVersion % "test" classifier "tests",
      "org.awaitility" % "awaitility-scala" % "3.0.0" % "test",
      "org.scalatest" %% "scalatest" % "3.0.4" % "test"
    )
  )

lazy val opentracingVersion = "0.31.0"
lazy val akkaVersion = "2.5.9"

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
