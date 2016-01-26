import scalariform.formatter.preferences._

name := "news"

version := "0.2.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  filters,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "com.mohiva" %% "play-silhouette" % "3.0.4",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "org.webjars" % "jquery" % "2.2.0",
  "org.webjars" % "bootstrap" % "3.3.6",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.5-P24",
  specs2 % Test,
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.4" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Play settings
routesGenerator := InjectedRoutesGenerator

// Compiler settings
scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

// Scalariform
defaultScalariformSettings

// Sbt Web
pipelineStages := Seq(digest, gzip)

// Skip Scaladoc
sources in (Compile,doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false