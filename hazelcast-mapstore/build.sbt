name := "hazelcast-mapstore"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast" % "3.1.5",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
