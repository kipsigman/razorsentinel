package models

import org.scalatest.Matchers
import org.scalatest.WordSpec

class TagContentSpec extends WordSpec with Matchers with TestData {

  "TagReplacement.replace" should {
    "replace repeated tags in text" in {
      val tag = "{firstname}"
      val str = tag + " is the worst bowler. " + tag + " really sucks."
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replace(str) shouldBe "Kip is the worst bowler. Kip really sucks."
    }
  }
  
  "TagReplacement.inlineEditReplace" should {
    "replace unset tag in inline edit HTML" in {
      val tag = "{first}"
      val str = """<a href="#" class="field-editable" data-type="text" data-name="{first}" data-value="first">{first}</a> found in dumpster"""
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.inlineEditReplace(str) shouldBe """<a href="#" class="field-editable" data-type="text" data-name="{first}" data-value="Kip">Kip</a> found in dumpster"""
    }
  }
  
  "TagContent.inlineEditHtml" should {
    "Wrap tags with inline edit HTML" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val contentForEditable = TagContent.inlineEditHtml(content, Set())
      contentForEditable shouldBe """Local Company declares <a href="#" class="field-editable" data-type="text" data-name="{firstname}" data-value="firstname">{firstname}</a>&nbsp;<a href="#" class="field-editable" data-type="text" data-name="{lastname}" data-value="lastname">{lastname}</a> Most Worthless Employee"""
    }
    
    "Wrap tags with inline edit HTML & replace set values" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val tagReplacementSet = Set(TagReplacement("{firstname}", "Kip"))
      val contentForEditable = TagContent.inlineEditHtml(content, tagReplacementSet)
      contentForEditable shouldBe """Local Company declares <a href="#" class="field-editable" data-type="text" data-name="{firstname}" data-value="Kip">Kip</a>&nbsp;<a href="#" class="field-editable" data-type="text" data-name="{lastname}" data-value="lastname">{lastname}</a> Most Worthless Employee"""
    }
  }

}
