package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

import models.auth.User

@RunWith(classOf[JUnitRunner])
class UserOwnedEntitySpec extends Specification with TestData {
  val userOwnedEntity: UserOwnedEntity = article
  val anonymousEntity: UserOwnedEntity = article.copy(userId = None)
  
  "isOwnedBy" should {
    "return true if userId is defined and matches user" in {
      userOwnedEntity.userId.isDefined must beTrue
      user.id.isDefined must beTrue
      userOwnedEntity.userId must equalTo(user.id)
      userOwnedEntity.isOwnedBy(user) must beTrue
    }
    "return false if userId is defined but does not match user" in {
      userOwnedEntity.userId.isDefined must beTrue
      user2.id.isDefined must beTrue
      userOwnedEntity.isOwnedBy(user2) must beFalse
    }
    "return false if userId is not defined" in {
      anonymousEntity.userId must beNone
      anonymousEntity.isOwnedBy(user) must beFalse
    }
  }
  
  "canEdit" should {
    "return true if userId is defined and matches user" in {
      userOwnedEntity.userId.isDefined must beTrue
      user.id.isDefined must beTrue
      userOwnedEntity.userId must equalTo(user.id)
      userOwnedEntity.canEdit(user) must beTrue
    }
    "return false if userId is defined but does not match user" in {
      userOwnedEntity.userId.isDefined must beTrue
      user2.id.isDefined must beTrue
      userOwnedEntity.canEdit(user2) must beFalse
    }
    "return true if userId is not defined" in {
      anonymousEntity.userId must beNone
      anonymousEntity.canEdit(user) must beTrue
    }
  }
}