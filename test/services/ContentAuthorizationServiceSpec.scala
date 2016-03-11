package services

import org.scalatest.Finders
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

import kipsigman.domain.entity.Status
import models._

class ContentAuthorizationServiceSpec extends WordSpec with Matchers with MockitoSugar with models.TestData {
  val draftEntity: Article = article
  val publishedEntity: Article = article.updateStatus(Status.Unlisted)
  
  val modelRepository: ModelRepository = mock[ModelRepository]
  val contentAuthorizationService = new ContentAuthorizationService(modelRepository)
  
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