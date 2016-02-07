package models

import org.scalatest.Matchers
import org.scalatest.WordSpec

import kipsigman.play.auth.entity.User

class ContentEntitySpec extends WordSpec with Matchers with TestData {
  val draftEntity: ContentEntity[Article] = article
  val publishedEntity: ContentEntity[Article] = article.publishUnlisted
  
  "canView" should {
    "return true if published" in {
      publishedEntity.canView(user) shouldBe true
      publishedEntity.canView(None) shouldBe true
    }
    "return true if user is owner" in {
      draftEntity.canView(user) shouldBe true
    }
    "return false if Draft and user is not owner" in {
      draftEntity.canView(user2) shouldBe false
    }
    "return false if Draft and no user" in {
      draftEntity.canView(None) shouldBe false
    }
  }
}