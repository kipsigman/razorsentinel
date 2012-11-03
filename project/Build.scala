import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "news"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.squeryl" %% "squeryl" % "0.9.5-3",
    "mysql" % "mysql-connector-java" % "5.1.21"
  )
  
  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings( // Add your own project settings here
      resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
  )

}
