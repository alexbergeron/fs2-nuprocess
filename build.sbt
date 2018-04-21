import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.github.alexbergeron",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "fs2-nuprocess",
    libraryDependencies ++= Seq(
      catsEffects,
      fs2Core,
      nuProcess,
      scalaTest % Test
    )
  )
