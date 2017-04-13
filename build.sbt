name := "akka-gpio"
organization := "org.lolhens"
version := "1.0.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.17",
  "com.pi4j" % "pi4j-core" % "1.1"
)

dependencyUpdatesExclusions := moduleFilter(organization = "org.scala-lang")

scalacOptions ++= Seq("-Xmax-classfile-name", "254")

publishTo := Some(Resolver.file("file", new File("target/releases")))
