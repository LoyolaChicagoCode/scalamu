organization := "edu.luc.etl"

name := "scalamu"

version := "0.4.4"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.6", "2.11.8")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalacOptions ++= Seq(
  "-deprecation", "-feature", "-unchecked", 
  "-language:higherKinds", "-language:implicitConversions"
)

scalacOptions in (Compile, doc) ++= Seq(
  "-groups", "-implicits",
  "-sourcepath", baseDirectory.value.toString,
  "-doc-source-url", "https://github.com/LoyolaChicagoCode/scalamu/tree/master€{FILE_PATH}.scala"
)

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.2.8",
  "org.typelevel" %% "shapeless-scalaz" % "0.4",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.6" % Test,
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.2.8" % Test
)

autoAPIMappings := true

apiURL := Some(url("http://loyolachicagocode.github.io/scalamu/doc"))

initialCommands in console := """
                                |import scalaz._
                                |import Scalaz._
                                |import scalamu._
                                |""".stripMargin
