package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

import models.auth.User

@RunWith(classOf[JUnitRunner])
class ContentEntitySpec extends Specification with TestData {
  val contentEntity: ContentEntity = article
  val publishedEntity: ContentEntity = article.publishPublic
  val anonymousEntity: ContentEntity = article.copy(userId = None)
  
  "canView" should {
    "return true if published" in {
      publishedEntity.canView(user) must beTrue
      publishedEntity.canView(None) must beTrue
    }
    "return true if user is owner" in {
      contentEntity.canView(user) must beTrue
    }
    "return false if Draft and user is not owner" in {
      contentEntity.canView(user2) must beFalse
    }
    "return false if Draft and no owner" in {
      anonymousEntity.canView(user) must beFalse
      anonymousEntity.canView(None) must beFalse
    }
  }
}