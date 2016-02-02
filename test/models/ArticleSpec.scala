package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

import models.auth.User
import models.ContentEntity.Status

@RunWith(classOf[JUnitRunner])
class ArticleSpec extends Specification with TestData {
  "status changes" should {
    "allow Deleted from any status" in {
      article.delete.status must equalTo(Status.Deleted)
    }
    "allow Public from Draft" in {
      article.publishPublic.status must equalTo(Status.Public)
    }
    "allow Unlisted from Draft" in {
      article.publishUnlisted.status must equalTo(Status.Unlisted)
    }
    "allow Public from Unlisted" in {
      article.publishUnlisted.publishPublic.status must equalTo(Status.Public)
    }
    "allow Unlisted from Public" in {
      article.publishPublic.publishUnlisted.status must equalTo(Status.Unlisted)
    }
    "allow Draft from Unlisted" in {
      article.publishUnlisted.revertToDraft.status must equalTo(Status.Draft)
    }
    "allow Draft from Public" in {
      article.publishPublic.revertToDraft.status must equalTo(Status.Draft)
    }
    "not allow any status change from Deleted" in {
      val deletedArticle = article.delete
      deletedArticle.publishPublic must throwA[AssertionError]
      deletedArticle.publishUnlisted must throwA[AssertionError]
      deletedArticle.revertToDraft must throwA[AssertionError]
    }
    "not allow Draft from itself" in {
      article.revertToDraft must throwA[AssertionError]
    }
  }
  
  "relativeUrl" should {
    "make headline into an seo url path with id" in {
      val editedArticle = article.
        addTagReplacement(TagReplacement("{firstname}", "Kip")).
        addTagReplacement(TagReplacement("{lastname}", "Sigman"))

      val completeArticle = editedArticle.addTagReplacement(TagReplacement("{city}", "Santa Barbara"))
      val inflatedArticle = ArticleInflated(completeArticle, articleTemplate)
      inflatedArticle.relativeUrl must equalTo("/articles/88-santa-barbara-kip-sigman-is-the-worst-bowler")
    }
  }

}