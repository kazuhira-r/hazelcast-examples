name := "hazelcast-configuration"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast" % "3.1.3",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
