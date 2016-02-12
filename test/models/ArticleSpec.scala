package models

import org.scalatest.Matchers
import org.scalatest.WordSpec

import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User
import models.Content.Status

class ArticleSpec extends WordSpec with Matchers with TestData {
  
  "status changes" should {
    "allow Deleted from any status" in {
      article.delete(editor).status shouldBe Status.Deleted
    }
    "allow Public from Draft" in {
      article.publishPublic(editor).status shouldBe Status.Public
    }
    "allow Unlisted from Draft" in {
      article.publishUnlisted(editor).status shouldBe Status.Unlisted
    }
    "allow Public from Unlisted" in {
      article.publishUnlisted(editor).publishPublic(editor).status shouldBe Status.Public
    }
    "allow Unlisted from Public" in {
      article.publishPublic(editor).publishUnlisted(editor).status shouldBe Status.Unlisted
    }
    "allow Draft from Unlisted" in {
      article.publishUnlisted(editor).revertToDraft(editor).status shouldBe Status.Draft
    }
    "allow Draft from Public" in {
      article.publishPublic(editor).revertToDraft(editor).status shouldBe Status.Draft
    }
    "not allow any status change from Deleted" in {
      val deletedArticle = article.delete(editor)
      an [IllegalStatusChangeException] should be thrownBy deletedArticle.publishPublic(editor) 
      an [IllegalStatusChangeException] should be thrownBy deletedArticle.publishUnlisted(editor)
      an [IllegalStatusChangeException] should be thrownBy deletedArticle.revertToDraft(editor)
    }
  }
  
}