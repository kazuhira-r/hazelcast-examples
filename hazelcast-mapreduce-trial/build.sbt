name := "hazelcast-mapreduce-trial"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

// javaOptions in Test += "-javaagent:/usr/local/byteman/current/lib/byteman.jar=script:rule.btm"

// fork in Test := true

// connectInput := true

parallelExecution in Test := false

libraryDependencies ++= Seq(
  "com.hazelcast" % "hazelcast" % "3.2-RC2",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
