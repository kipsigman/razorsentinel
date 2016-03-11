package services

import org.scalatest.Matchers
import org.scalatest.WordSpec

class StringServiceSpec extends WordSpec with Matchers with models.TestData {

  "formatSeo" should {
    "make string into a url friendly path" in {
      val str = "Santa Barbara - Kip Sigman is the worst bowler."
      StringService.formatSeo(str) shouldBe "santa-barbara-kip-sigman-is-the-worst-bowler"
    }
  }
}