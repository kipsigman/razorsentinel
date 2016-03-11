package models

import com.mohiva.play.silhouette.api.LoginInfo
import kipsigman.domain.entity.Content
import kipsigman.domain.entity.Role
import kipsigman.domain.entity.Status
import kipsigman.play.auth.entity.User

trait TestData {
  val user = User(Option(66), LoginInfo("", ""), Option("Johnny"), Option("Utah"), "johnny.utah@fbi.gov", None, Set(Role.Member))
  implicit val userOption: Option[User] = Option(user)
  val editor: Option[User] = Option(user.copy(roles = user.roles + Role.Editor))
  
  val user2 = User(Option(67), LoginInfo("", ""), Option("Angelo"), Option("Pappas"), "angelo.pappas@fbi.gov", None, Set())
  
  val articleTemplate = {
    val headline = "{city} - {firstname} {lastname} is the worst bowler."
    val body = "<p>{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks.</p> <p>{lastname} tries hard though.</p>"
    ArticleTemplate(Option(13), user.id.get, Status.Public, Seq(NewsCategoryOptions.Sports), headline, body)  
  }
  
  val largeBodyTemplate = {
    val headline = "Tourist Attacks Mickey Mouse"
    val body = """<p>Anaheim, CA - Disgruntled, and assumed insane tourist, {first} {last}, was apprehended by Disneyland security last Thursday when he began to beat on the beloved Mickey Mouse character, long-time icon of the Disney franchise.</p>
      <p>The incident occured while Mr. {last} and family were walking down "Main Street, USA," a popular area of the Disneyland park, where many of the life-sized, costumed characters greet and interact with visitors.  Apparently {last} did not welcome the approach of Mickey Mouse, and reacted violently to the mouse's popular "love hug."</p>
      <p>Mr. {last} grabbed Mickey around the neck and held him in a wrestler's choke hold while repeatedly punching him in the face and gut.  Children screamed as the beloved icon fell to the ground, where {last} continued to kick the character in the groin.</p>
      <p>Within moments, park security converged on the disturbance and restrained {last}, while other officers pulled the downed Mickey to safety.  Mr. {last} was quickly cuffed and thown into the park's holding room, while Anaheim police were called to remove and arrest the disgrunted park visitor.</p>
      <p>{first} {last} has since filed a lawsuit agaist The Walt Disney Co. for sexual harassment by Mickey Mouse, and for wrongfull expulsion from the park.  He's suing for $10,000,000 plus a credit for $44.95 (entrance fee to the park, lost during the incident).</p>
      <p>Parents of children who witnessed the event are encouraged to have their children call a special counseling hot-line set up by the Disney Company, (714) 555-4000.</p>"""
    ArticleTemplate(Option(20), user.id.get, Status.Public, Seq(NewsCategoryOptions.National), headline, body)  
  }
  
  val article = Article(Option(88), user.id, articleTemplate, Status.Draft, Set())
}