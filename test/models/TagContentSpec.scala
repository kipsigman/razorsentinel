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
class TagContentSpec extends Specification with TestData {

  "TagReplacement.replace" should {
    "replace repeated tags in text" in {
      val tag = "{firstname}"
      val str = tag + " is the worst bowler. " + tag + " really sucks."
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replace(str) must equalTo("Kip is the worst bowler. Kip really sucks.")
    }
  }
  
  "TagReplacement.inlineEditReplace" should {
    "replace unset tag in inline edit HTML" in {
      val tag = "{first}"
      val str = """<a href="#" class="field-editable" data-type="text" data-name="{first}" data-value="first">{first}</a> found in dumpster"""
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.inlineEditReplace(str) must equalTo("""<a href="#" class="field-editable" data-type="text" data-name="{first}" data-value="first">Kip</a> found in dumpster""")
    }
  }
  
  "TagContent.inlineEditHtml" should {
    "Wrap tags with inline edit HTML" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val contentForEditable = TagContent.inlineEditHtml(content, Set())
      contentForEditable must equalTo("""Local Company declares <a href="#" class="field-editable" data-type="text" data-name="{firstname}" data-value="firstname">{firstname}</a> <a href="#" class="field-editable" data-type="text" data-name="{lastname}" data-value="lastname">{lastname}</a> Most Worthless Employee""")
    }
    
    "Wrap tags with inline edit HTML & replace set values" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val tagReplacementSet = Set(TagReplacement("{firstname}", "Kip"))
      val contentForEditable = TagContent.inlineEditHtml(content, tagReplacementSet)
      contentForEditable must equalTo("""Local Company declares <a href="#" class="field-editable" data-type="text" data-name="{firstname}" data-value="firstname">Kip</a> <a href="#" class="field-editable" data-type="text" data-name="{lastname}" data-value="lastname">{lastname}</a> Most Worthless Employee""")
    }
  }

}
