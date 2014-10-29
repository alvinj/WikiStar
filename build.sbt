import AssemblyKeys._

assemblySettings

// first two lines are for sbt-assembly

name := "WikiStar"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions += "-deprecation"


