package models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

/**
 * @author kip
 */
class ArticleSpec extends Specification with test.BaseSpec {
  
  "addTagReplacement" should {
    "put data into tagReplacements field" in {
      running(fakeApp) {
        val user = User.save(User(Entity.UnpersistedId, "Kip", "kip.sigman@gmail.com", "passwd", Permission.Administrator))
        val headline = "{city} - {firstname} {lastname} is the worst bowler."
        val body = "{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks."
        val articleTemplate = ArticleTemplate.save(ArticleTemplate(Entity.UnpersistedId, headline, body))
        
        val articleId = Article.save(Article(Entity.UnpersistedId, articleTemplate.id, None, false)).id
        var article = Article.findByIdInflated(articleId)
        
        article = article.addTagReplacement(TagReplacement("{firstname}","Kip"))
        article.tagReplacements.get must equalTo("""{"{firstname}":"Kip"}""")
        article.tagReplacementSet.size must equalTo(1)
        article.tagReplacementSet.head.tag must equalTo("{firstname}")
        article.tagReplacementSet.head.replacement must equalTo("Kip")
        
        article = article.addTagReplacement(TagReplacement("{lastname}","Sigman"))
        article.tagReplacements.get must equalTo("""{"{firstname}":"Kip","{lastname}":"Sigman"}""")
        article.tagReplacementSet.size must equalTo(2)
      }
    }
  }
  
  "relativeUrl" should {
    "make headline into an seo url path with id" in {
      running(fakeApp) {
        val user = User.save(User(Entity.UnpersistedId, "Kip", "kip.sigman@gmail.com", "passwd", Permission.Administrator))
        val headline = "{city} - {firstname} {lastname} is the worst bowler."
        val body = "{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks."
        val articleTemplate = ArticleTemplate.save(ArticleTemplate(Entity.UnpersistedId, headline, body))
        
        val articleId = Article.save(Article(Entity.UnpersistedId, articleTemplate.id, None, false)).id
        var article = Article.findByIdInflated(articleId)
        article = article.addTagReplacement(TagReplacement("{firstname}","Kip"))
        article = article.addTagReplacement(TagReplacement("{lastname}","Sigman"))
        article = Article.save(article)
        article.publish must beFalse
        article = article.addTagReplacement(TagReplacement("{city}","Santa Barbara"))
        Article.save(article)
        val inflatedArticle = Article.findByIdInflated(articleId)
        inflatedArticle.publish must beTrue
        inflatedArticle.relativeUrl must equalTo("/articles/santa-barbara-kip-sigman-is-the-worst-bowler-1")
      }
    }
  }

}