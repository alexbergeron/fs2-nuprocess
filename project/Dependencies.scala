import sbt._

object Dependencies {
  lazy val catsEffects = "org.typelevel" %% "cats-effect" % "1.0.0-RC"
  lazy val fs2Core = "co.fs2" %% "fs2-core" % "0.10.3"
  lazy val fs2Io = "co.fs2" %% "fs2-io" % "0.10.3"
  lazy val nuProcess = "com.zaxxer" % "nuprocess" % "1.1.3"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
