package models

import org.scalatest.Matchers
import org.scalatest.WordSpec
import kipsigman.domain.entity.Role
import kipsigman.domain.entity.Status
import kipsigman.play.auth.entity.User

class ArticleContentSpec extends WordSpec with Matchers with TestData {
  
  val articleContent = largeBodyTemplate
  
  "bodyTeaser" should {
    "trim to end of last paragraph" in {
      val str = articleContent.bodyTeaser(500)
      str.length() should be < 500 
      str should startWith("<p>")
      str should endWith("</p>")
    }
    
    "add opening paragraph tag" in {
      val invalidBody = articleContent.body.replaceFirst("<p>", "")
      val invalidAC = articleContent.copy(body = invalidBody)
      val str = invalidAC.bodyTeaser(500)
      str should startWith("<p>")
      str should endWith("</p>")
    }
    
    "add opening & closing paragraph tags" in {
      val invalidBody = articleContent.body.replaceAll("<p>", "").replaceAll("</p>", "")
      val invalidAC = articleContent.copy(body = invalidBody)
      val str = invalidAC.bodyTeaser(500)
      str should startWith("<p>")
      str should endWith("</p>")
    }
    
    "end with complete sentence" in {
      val invalidBody = articleContent.body.replaceAll("</p>", "")
      val invalidAC = articleContent.copy(body = invalidBody)
      val str = invalidAC.bodyTeaser(500)
      str should startWith("<p>")
      str should endWith(".</p>")
    }
  }
}