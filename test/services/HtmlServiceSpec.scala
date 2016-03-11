package services

import org.scalatest.Matchers
import org.scalatest.WordSpec
import kipsigman.play.service.HtmlService

class HtmlServiceSpec extends WordSpec with Matchers with models.TestData {
  val htmlService = new HtmlService()
  
  val bodyFragmentValid = """<p>Sentence one. Sentence two.</p><p>Sentence three.</p>"""
  val bodyFragmentNoClosingP = """<p>Sentence one. Sentence two.</p><p>Sentence three."""
  
  "repairBodyFragment" should {
    val expectedRepairedFragment = "<p>Sentence one. Sentence two.</p>\n<p>Sentence three.</p>"
    
    "add newlines for separating elements" in {
      htmlService.repairBodyFragment(bodyFragmentValid) shouldBe expectedRepairedFragment
    }
    
    "add closing tags" in {
      htmlService.repairBodyFragment(bodyFragmentNoClosingP) shouldBe expectedRepairedFragment
    }
    
    "remove unmatched closing tag" in {
      val bodyFragment = 
        """<div class="row">
             Some text with a <a href="http://www.razorsentinel.com/help">link</a> and <img src="/images/10.jpg">.
           </div>
           </div>"""
      
      val repaired = htmlService.repairBodyFragment(bodyFragment)
      repaired.startsWith("""<div class="row">""")
      repaired.endsWith("""</div>""")
      repaired.replace("</div>", "").contains("</div>") shouldBe false
    }
  }
  
  "validateBodyFragment" should {
    "return true for valid HTML" in {
      val result = htmlService.validateBodyFragment(bodyFragmentValid)
      result.valid shouldBe true
    }
    
    "return false for missing closing tag" in {
      val result = htmlService.validateBodyFragment(bodyFragmentNoClosingP)
      result.valid shouldBe false
      result.errorMessage shouldBe Some("""Error on line 1: The element type "p" must be terminated by the matching end-tag "</p>".""")
    }
  }

}