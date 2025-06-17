name := """epam-study-app"""

ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.16"

val http4sVersion = "0.23.18"
val http4sBlaze = "0.23.13"
val awsSdkVersion = "2.25.14"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sBlaze,
  "org.http4s" %% "http4s-blaze-client" % http4sBlaze,
  "software.amazon.awssdk" % "ec2" % awsSdkVersion,
  "software.amazon.awssdk" % "s3" % awsSdkVersion,
  "software.amazon.awssdk" % "auth" % awsSdkVersion,
  "software.amazon.awssdk" % "regions" % awsSdkVersion,
  "software.amazon.awssdk" % "netty-nio-client" % awsSdkVersion,
  "software.amazon.awssdk" % "sqs" % awsSdkVersion,
  "software.amazon.awssdk" % "sns" % awsSdkVersion,
  "software.amazon.awssdk" % "auth" % awsSdkVersion,
  "software.amazon.awssdk" % "lambda"   % awsSdkVersion,
  "ch.qos.logback" % "logback-classic" % "1.5.13",
  "com.typesafe.slick" %% "slick" % "3.6.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1",
  "org.postgresql" % "postgresql" % "42.7.2",
  "co.fs2" %% "fs2-io" % "3.10.1",
  "com.typesafe.play" %% "play-json" % "2.10.0-RC9",
  "com.sun.mail" % "jakarta.mail" % "2.0.1",
  "eu.timepit" %% "fs2-cron-core" % "0.7.0",
  "eu.timepit" %% "fs2-cron-cron4s" % "0.7.0",
)

import sbtassembly.AssemblyPlugin.autoImport.*

assembly / assemblyJarName := s"${name.value}-${version.value}.jar"
assembly / mainClass := Some("org.main.AppServer")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ =>
    // For all the other files, use the default sbt-assembly merge strategy
    MergeStrategy.first
}

Compile / run / fork := true
