import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.dominodatalab",
      scalaVersion := "2.11.11",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "fs2-nuprocess",
    libraryDependencies ++= Seq(
      fs2Core,
      nuProcess,
      scalaTest % Test
    )
  )
