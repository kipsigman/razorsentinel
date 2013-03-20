package util

import org.specs2.mutable._

class StringsSpec extends Specification {
  
  "formatSeo" should {
    "make string into a url friendly path" in {
      
      val str = "Santa Barbara - Kip Sigman is the worst bowler."
      Strings.formatSeo(str) must equalTo("santa-barbara-kip-sigman-is-the-worst-bowler")
    }
  }

}