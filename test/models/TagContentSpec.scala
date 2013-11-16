package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

/**
 * @author kip
 */
@RunWith(classOf[JUnitRunner])
class TagContentSpec extends Specification {
  
  "TagContent" should {
    "contentHtmlInlineEdit wrap tags in editable HTML" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val contentForEditable = TagContent.contentHtmlInlineEdit(content)
      contentForEditable must equalTo("""Local Company declares <a href="#" class="field-editable" data-type="text" data-name="{firstname}" data-value="firstname">{firstname}</a> <a href="#" class="field-editable" data-type="text" data-name="{lastname}" data-value="lastname">{lastname}</a> Most Worthless Employee""")
    }
  }
  
  "TagReplacement" should {
    "replace repeated tags in text" in {
      val tag = "{firstname}"
      val str = tag + " is the worst bowler. " + tag + " really sucks."
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replace(str) must equalTo("Kip is the worst bowler. Kip really sucks.")
    }
  }
  
}
