name := "hazelcast-session-clustering"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

seq(webSettings :_*)

artifactName := { (version: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container",
  "org.eclipse.jetty" % "jetty-plus"   % "9.1.0.v20131115" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
  "com.hazelcast" % "hazelcast-wm" % "3.1.3"
)
