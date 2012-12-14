import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "news"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "jp.t2v" % "play20.auth_2.9.1" % "0.3",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "org.squeryl" %% "squeryl" % "0.9.5-5",
    "mysql" % "mysql-connector-java" % "5.1.21",
    "se.radley" %% "play-plugins-enumeration" % "1.0.1"
  )
  
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings( // Add your own project settings here
      resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
  )

}
