name := "spring-boot-hazelcast-distexec"

version := "0.0.1-SNAPSHOT"

organization := "org.littlewings"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-Xlint", "-unchecked", "-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter-web" % "1.2.2.RELEASE",
  "com.hazelcast" % "hazelcast" % "3.4.1",
  "com.hazelcast" % "hazelcast-spring" % "3.4.1"
)
