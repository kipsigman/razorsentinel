import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "news"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    "jp.t2v" %% "play21.auth" % "0.7",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.squeryl" %% "squeryl" % "0.9.5-6",
    "mysql" % "mysql-connector-java" % "5.1.21",
    "com.h2database" % "h2" % "1.2.127" % "test"
  )
  
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    // resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
  )

}
