import sbt._
import Keys._

object BuildSettings {
  val scalaVer = "2.10.3"
  val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaVer
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.5"
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.5"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := "io.segl",
    version       := "0.0.1-SNAPSHOT",
    scalaVersion  := scalaVer
  )
}

object SeglMacroBuild extends Build {
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings
  ) aggregate(macros, examples)

  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaReflect, slf4j))
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(slf4j, slf4jSimple))
  ) dependsOn macros
}