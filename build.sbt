name := """ec2-web"""


ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val http4sVersion = "0.23.18"
val http4sBlaze = "0.23.13"

libraryDependencies += "software.amazon.awssdk" % "ec2" % "2.25.14" // or latest version

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sBlaze,
  "org.http4s" %% "http4s-blaze-client" % http4sBlaze
)

import sbtassembly.AssemblyPlugin.autoImport._

assembly / assemblyJarName := s"${name.value}-assembly-${version.value}.jar"
assembly / mainClass := Some("org.main.AppServer")


 assembly / assemblyMergeStrategy := {
   case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
    // Keep the content for all reference-overrides.conf files
    MergeStrategy.concat
  case _ =>
    // For all the other files, use the default sbt-assembly merge strategy
    MergeStrategy.first
}
