name := "hazelcast-data-affinity"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.0"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

incOptions := incOptions.value.withNameHashing(true)

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast" % "3.2",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test"
)
