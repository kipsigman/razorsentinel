package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ArticleSpec extends Specification {

  "relativeUrl" should {
    "make headline into an seo url path with id" in {
      val headline = "{city} - {firstname} {lastname} is the worst bowler."
      val body = "{firstname} {lastname} is the worst bowler in {city}. {firstname} really sucks."
      val articleTemplate = ArticleTemplate(Option(1), headline, body)
      val article = Article(Option(88), None, articleTemplate.id.get, None, false).
        addTagReplacement(TagReplacement("{firstname}", "Kip")).
        addTagReplacement(TagReplacement("{lastname}", "Sigman"))

      article.publish must beFalse

      val completeArticle = article.addTagReplacement(TagReplacement("{city}", "Santa Barbara"))
      val inflatedArticle = ArticleInflated(completeArticle, articleTemplate)
      inflatedArticle.relativeUrl must equalTo("/articles/88-santa-barbara-kip-sigman-is-the-worst-bowler")
    }
  }

}