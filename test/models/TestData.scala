package models

import com.mohiva.play.silhouette.api.LoginInfo
import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User

trait TestData {
  val user = User(Option(66), LoginInfo("", ""), Option("Johnny"), Option("Utah"), "johnny.utah@fbi.gov", None, Set(Role.Member))
  implicit val userOption: Option[User] = Option(user)
  val editor: Option[User] = Option(user.copy(roles = user.roles + Role.Editor))
  
  val user2 = User(Option(67), LoginInfo("", ""), Option("Angelo"), Option("Pappas"), "angelo.pappas@fbi.gov", None, Set())
  
  val articleTemplate = {
    val headline = "{city} - {firstname} {lastname} is the worst bowler."
    val body = "{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks."
    ArticleTemplate(Option(13), user.id.get, ContentEntity.Status.Public, NewsCategoryOptions.Sports, headline, body)  
  }
  
  val article = Article(Option(88), user.id.get, articleTemplate.id.get, ContentEntity.Status.Draft, Set())
}