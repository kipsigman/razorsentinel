package services

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

@RunWith(classOf[JUnitRunner])
class StringServiceSpec extends Specification {

  "formatSeo" should {
    "make string into a url friendly path" in {

      val str = "Santa Barbara - Kip Sigman is the worst bowler."
      StringService.formatSeo(str) must equalTo("santa-barbara-kip-sigman-is-the-worst-bowler")
    }
  }

}