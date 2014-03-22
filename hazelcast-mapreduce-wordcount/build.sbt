name := "hazelcast-mapreduce-wordcount"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "org.apache.lucene" % "lucene-analyzers-kuromoji" % "4.7.0",
  "com.hazelcast" % "hazelcast" % "3.2",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0" % "test",
  "org.jsoup" % "jsoup" % "1.7.3" % "test",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
