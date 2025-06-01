name := """epam-study-app"""

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"

val http4sVersion = "0.23.18"
val http4sBlaze = "0.23.13"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sBlaze,
  "org.http4s" %% "http4s-blaze-client" % http4sBlaze,
  "software.amazon.awssdk" % "ec2" % "2.25.14",
  "software.amazon.awssdk" % "s3" % "2.25.14",
  "software.amazon.awssdk" % "auth" % "2.25.14",
  "software.amazon.awssdk" % "regions" % "2.25.14",
  "ch.qos.logback" % "logback-classic" % "1.5.13",
  "com.typesafe.slick" %% "slick" % "3.6.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1",
  "org.postgresql" % "postgresql" % "42.7.2",
  "co.fs2" %% "fs2-io" % "3.10.1"
)

import sbtassembly.AssemblyPlugin.autoImport.*

assembly / assemblyJarName := s"${name.value}-assembly-${version.value}.jar"
assembly / mainClass := Some("org.main.AppServer")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ =>
    // For all the other files, use the default sbt-assembly merge strategy
    MergeStrategy.first
}
