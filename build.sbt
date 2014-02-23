organization := "edu.luc.etl"

name := "scalamu"

version := "0.2.0"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

seq(bintrayPublishSettings:_*)

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:higherKinds")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.5",
  "org.scalatest" % "scalatest_2.10" % "2.0.1-SNAP" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.1" % "test"
)

apiURL := Some(url("http://lucproglangcourse.bitbucket.org/scalamu/doc/"))

initialCommands in console := """
                                |import scalaz._
                                |import Scalaz._
                                |import scalamu._
                                |""".stripMargin