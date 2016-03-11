package models

import org.scalatest.Finders
import org.scalatest.Matchers
import org.scalatest.WordSpec

import kipsigman.domain.entity.IllegalStatusChangeException
import kipsigman.domain.entity.Status

class ArticleSpec extends WordSpec with Matchers with TestData {
  
  "status changes" should {
    "allow Deleted from any status" in {
      article.updateStatus(Status.Deleted)(editor).status shouldBe Status.Deleted
    }
    "allow Public from Draft" in {
      article.updateStatus(Status.Public)(editor).status shouldBe Status.Public
    }
    "allow Unlisted from Draft" in {
      article.updateStatus(Status.Unlisted)(editor).status shouldBe Status.Unlisted
    }
    "allow Public from Unlisted" in {
      article.updateStatus(Status.Unlisted)(editor).updateStatus(Status.Public)(editor).status shouldBe Status.Public
    }
    "allow Unlisted from Public" in {
      article.updateStatus(Status.Public)(editor).updateStatus(Status.Unlisted)(editor).status shouldBe Status.Unlisted
    }
    "allow Draft from Unlisted" in {
      article.updateStatus(Status.Unlisted)(editor).updateStatus(Status.Draft)(editor).status shouldBe Status.Draft
    }
    "allow Draft from Public" in {
      article.updateStatus(Status.Public)(editor).updateStatus(Status.Draft)(editor).status shouldBe Status.Draft
    }
    "not allow any status change from Deleted" in {
      val deletedArticle = article.updateStatus(Status.Deleted)(editor)
      an [IllegalStatusChangeException] should be thrownBy deletedArticle.updateStatus(Status.Public)(editor) 
      an [IllegalStatusChangeException] should be thrownBy deletedArticle.updateStatus(Status.Unlisted)(editor)
      an [IllegalStatusChangeException] should be thrownBy deletedArticle.updateStatus(Status.Draft)(editor)
    }
  }
  
}