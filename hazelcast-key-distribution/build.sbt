name := "hazelcast-key-distribution"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast" % "3.1.6",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
