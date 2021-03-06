lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.kenjih",
      scalaVersion := "2.12.4"
    )),
    name := "scala-netty-http"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "io.netty" % "netty-all" % "4.1.24.Final"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.4"
