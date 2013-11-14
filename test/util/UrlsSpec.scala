package util

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.AnyContent
import play.api.mvc.AnyContentAsEmpty
import play.api.test._

@RunWith(classOf[JUnitRunner])
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
  
//  "shortenUrl" should {
//    "return a shortened url" in {
//      val shortUrlFuture = Urls.shortenUrl("http://www.thesith.com/vader.html")
//      shortUrlFuture.map(_ must equalTo("http://goo.gl/tZChR"))
//       
//    }
//  }
  
  "urlDecode" should {
    "decode string in URL" in {
      Urls.urlDecode("some+crap+to+be+in+a+querystring%3F%26") must equalTo("some crap to be in a querystring?&")
      Urls.urlDecode("http%3A%2F%2Fwww.thesith.com%2Fvader.html") must equalTo("http://www.thesith.com/vader.html")
    }
  }
  
  "urlEncode" should {
    "encode string for URL" in {
      Urls.urlEncode("some crap to be in a querystring?&") must equalTo("some+crap+to+be+in+a+querystring%3F%26")
      Urls.urlEncode("http://www.thesith.com/vader.html") must equalTo("http%3A%2F%2Fwww.thesith.com%2Fvader.html")
      Urls.urlDecode(Urls.urlEncode("http://www.thesith.com/vader.html")) must equalTo("http://www.thesith.com/vader.html")
    }
  }
  
}