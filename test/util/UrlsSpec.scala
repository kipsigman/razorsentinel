package util

import org.specs2.mutable._
import play.api.mvc.{AnyContent,AnyContentAsEmpty,Request}
import play.api.test._

class UrlsSpec extends Specification {
  
  "absoluteUrl" should {
    "make relativeUrl into a url friendly path" in {
      
      val relativeUrl = "/articles/kip-sigman-voted-most-worthless-employee-99"
      val headers = FakeHeaders(Seq(play.api.http.HeaderNames.HOST -> List("www.news.com")))
      val request = FakeRequest[AnyContent]("GET", "http://www.news.com/articles/updateTag", headers, AnyContentAsEmpty)
      // println("host="+request.host)
      
      Urls.absoluteUrl(request, relativeUrl) must equalTo("http://www.news.com/articles/kip-sigman-voted-most-worthless-employee-99")
    }
  }
  
  "isValidUrl" should {
    "match valid URL" in {
      Urls.isValidUrl("http://www.thesith.com/vader.html") must beTrue
      Urls.isValidUrl("www.thesith.com/vader.html") must beFalse
      Urls.isValidUrl("xxx http://www.thesith.com/vader.html") must beFalse
    }
  }

}