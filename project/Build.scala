import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "news"
  val appVersion      = "1.0"

  val appDependencies = Seq(
    jdbc,
    "jp.t2v" %% "play2.auth"      % "0.10.1" exclude("org.scala-stm", "scala-stm_2.10.0"),
    "jp.t2v" %% "play2.auth.test" % "0.10.1" % "test" exclude("org.scala-stm", "scala-stm_2.10.0"),
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.squeryl" %% "squeryl" % "0.9.5-6",
    "mysql" % "mysql-connector-java" % "5.1.21",
    "com.h2database" % "h2" % "1.2.127" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}