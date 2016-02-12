package models

import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

import kipsigman.play.auth.entity.User

import services.ContentAuthorizationService

class ContentAuthorizationServiceSpec extends WordSpec with Matchers with MockitoSugar with TestData {
  val draftEntity: Article = article
  val publishedEntity: Article = article.publishUnlisted
  
  val newsRepository: NewsRepository = mock[NewsRepository]
  val contentAuthorizationService = new ContentAuthorizationService(newsRepository)
  
  "canView" should {
    "return true if published" in {
      contentAuthorizationService.canView(publishedEntity) shouldBe true
      contentAuthorizationService.canView(publishedEntity)(None) shouldBe true
    }
    "return true if user is owner" in {
      contentAuthorizationService.canView(draftEntity) shouldBe true
    }
    "return false if Draft and user is not owner" in {
      contentAuthorizationService.canView(draftEntity)(Option(user2)) shouldBe false
    }
    "return false if Draft and no user" in {
      contentAuthorizationService.canView(draftEntity)(None) shouldBe false
    }
  }
}