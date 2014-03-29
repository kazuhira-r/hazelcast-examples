name := "hazelcast-getting-starged"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast" % "3.2"
)
