package models

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

import models.auth._

trait TestData {
  val user = User(Option(66), LoginInfo("", ""), Option("Johnny"), Option("Utah"), "johnny.utah@fbi.gov", None, Set())
  val user2 = User(Option(67), LoginInfo("", ""), Option("Angelo"), Option("Pappas"), "angelo.pappas@fbi.gov", None, Set())
  
  val articleTemplate = {
    val headline = "{city} - {firstname} {lastname} is the worst bowler."
    val body = "{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks."
    ArticleTemplate(Option(13), headline, body)  
  }
  
  val article = Article(Option(88), user.id, articleTemplate.id.get, ContentEntity.Status.Draft, None)
}