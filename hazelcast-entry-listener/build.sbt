name := "hazelcast-entry-listener"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast-wm" % "3.1.3",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
