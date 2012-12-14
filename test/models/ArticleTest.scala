package models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

/**
 * @author kip
 */
class ArticleTest extends Specification {
  
  "relativeUrl" should {
    "make headline into an seo url path with id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val user = User.save(User(Entity.UnpersistedId, "Kip", "kip.sigman@gmail.com", "passwd", Permission.Administrator))
        val headline = "{city} - {firstname} {lastname} is the worst bowler."
        val body = "{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks."
        val articleTemplate = ArticleTemplate.save(ArticleTemplate(Entity.UnpersistedId, user.id, headline, body))
        
        var article = Article(Entity.UnpersistedId, articleTemplate.id, None)
        article = article.addTagReplacement(TagReplacement("{firstname}","Kip"))
        article = article.addTagReplacement(TagReplacement("{lastname}","Sigman"))
        article = article.addTagReplacement(TagReplacement("{city}","Santa Barbara"))
        
        val inflatedArticle = Article.findByIdInflated(Article.save(article).id)
        inflatedArticle.relativeUrl must equalTo("/articles/santa-barbara-kip-sigman-is-the-worst-bowler-1")
      }
    }
  }

}