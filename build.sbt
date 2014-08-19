name := "news"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  jdbc,
  ws,
  "javax.inject" % "javax.inject" % "1",
  "com.google.inject" % "guice" % "3.0",
  "jp.t2v" %% "play2-auth"      % "0.12.0",
  "jp.t2v" %% "play2-auth-test" % "0.12.0" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.squeryl" %% "squeryl" % "0.9.5-7",
  "mysql" % "mysql-connector-java" % "5.1.32",
  "com.h2database" % "h2" % "1.4.179" % "test",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "font-awesome" % "4.1.0",
  "org.scalatestplus" %% "play" % "1.1.0" % "test"
)     

lazy val root = (project in file(".")).enablePlugins(PlayScala)