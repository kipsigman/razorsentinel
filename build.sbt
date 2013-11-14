name := "news"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "jp.t2v" %% "play2-auth"      % "0.11.0",
  "jp.t2v" %% "play2-auth-test" % "0.11.0" % "test",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.squeryl" %% "squeryl" % "0.9.5-6",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "com.h2database" % "h2" % "1.2.127" % "test"
)     

play.Project.playScalaSettings