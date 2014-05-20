import sbt.Keys._

name := "threatboard"

version := "0.0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "16.0.1",
  "com.fatboyindustrial.crowd-control" % "crowd-control" % "0.1.0",
  "com.fatboyindustrial.omnium" % "omnium-core" % "0.1.0",
  "com.sun.jersey" % "jersey-core" % "1.18.1"
)

play.Project.playJavaSettings
