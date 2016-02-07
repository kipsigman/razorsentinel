import com.typesafe.sbt.packager.docker._
import scalariform.formatter.preferences._

name := "news"

organization := "kipsigman"

version := "0.2.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  filters,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "kipsigman" %% "scala-domain-model" % "0.1.0",
  "kipsigman" %% "play-auth" % "0.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "org.webjars" % "jquery" % "2.2.0",
  "org.webjars" % "bootstrap" % "3.3.6",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.5-P24",
  "org.julienrf" %% "play-jsmessages" % "2.0.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.scalatestplus" %% "play" % "1.4.0" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

// Play settings
routesGenerator := InjectedRoutesGenerator

routesImport += "models.NewsCategoryOptions.queryStringBinder"
routesImport += "models.NewsCategoryOptions.pathBinder"

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

// Docker
maintainer in Docker := "Kip Sigman <kip.sigman@gmail.com>"

dockerExposedPorts := Seq(9000)

dockerBaseImage := "java:latest"

// Elastic Beanstalk tasks
lazy val elasticBeanstalkStage = taskKey[Unit]("Create a local directory with all the files for an AWS Elastic Beanstalk Docker distribution.")

elasticBeanstalkStage := {
  // Depends on docker:stage
  val dockerStageValue = (stage in Docker).value
  
  // Copy Elastic Beanstalk Dockerrun.aws.json configuration file to Docker target directory
  val elasticBeanstalkSource = baseDirectory.value / "elastic-beanstalk"
  IO.copyDirectory(elasticBeanstalkSource, dockerStageValue, true)
}

lazy val elasticBeanstalkDist = taskKey[File]("Creates a zip for an AWS Elastic Beanstalk Docker distribution")

elasticBeanstalkDist := {
  val log = streams.value.log
  
  // Depends on elasticBeanstalkStage
  val stageValue = elasticBeanstalkStage.value
  
  // Zip Docker target
  val dockerStagingDirectory: File = (stagingDirectory in Docker).value
  
  val zipFile: File = (target.value) / s"${name.value}-${version.value}-elastic-beanstalk.zip"
  log.info(s"Zipping $dockerStagingDirectory to $zipFile")
  Process(s"zip -r $zipFile .", dockerStagingDirectory) !!
  
  zipFile
}