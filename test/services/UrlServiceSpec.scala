package services

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock._
import org.junit.runner._
import org.mockito.Mockito.when
import org.slf4j.LoggerFactory

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.AnyContent
import play.api.mvc.AnyContentAsEmpty
import play.api.test._
import play.api.libs.ws.WSClient

@RunWith(classOf[JUnitRunner])
class UrlServiceSpec extends Specification with Mockito {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val mockWsClient = mock[WSClient]
  val urlService = new UrlService(mockWsClient)

  "absoluteUrl" should {
    "make relativeUrl into a url friendly path" in {

      val relativeUrl = "/articles/kip-sigman-voted-most-worthless-employee-99"
      val headers = FakeHeaders(Seq(play.api.http.HeaderNames.HOST -> "www.news.com"))
      val request = FakeRequest[AnyContent]("GET", "http://www.news.com/articles/updateTag", headers, AnyContentAsEmpty)
      logger.debug(s"host=${request.host}")

      urlService.absoluteUrl(request, relativeUrl) must equalTo("http://www.news.com/articles/kip-sigman-voted-most-worthless-employee-99")
    }
  }

  "isValidUrl" should {
    "match valid URL" in {
      urlService.isValidUrl("http://www.thesith.com/vader.html") must beTrue
      urlService.isValidUrl("www.thesith.com/vader.html") must beFalse
      urlService.isValidUrl("xxx http://www.thesith.com/vader.html") must beFalse
    }
  }

  "urlDecode" should {
    "decode string in URL" in {
      urlService.urlDecode("some+crap+to+be+in+a+querystring%3F%26") must equalTo("some crap to be in a querystring?&")
      urlService.urlDecode("http%3A%2F%2Fwww.thesith.com%2Fvader.html") must equalTo("http://www.thesith.com/vader.html")
    }
  }

  "urlEncode" should {
    "encode string for URL" in {
      urlService.urlEncode("some crap to be in a querystring?&") must equalTo("some+crap+to+be+in+a+querystring%3F%26")
      urlService.urlEncode("http://www.thesith.com/vader.html") must equalTo("http%3A%2F%2Fwww.thesith.com%2Fvader.html")
      urlService.urlDecode(urlService.urlEncode("http://www.thesith.com/vader.html")) must equalTo("http://www.thesith.com/vader.html")
    }
  }

  //  "shortenUrl" should {
  //    "return a shortened url" in {
  //      val shortUrlFuture = Urls.shortenUrl("http://www.thesith.com/vader.html")
  //      shortUrlFuture.map(_ must equalTo("http://goo.gl/tZChR"))
  //       
  //    }
  //  }

}