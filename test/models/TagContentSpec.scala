package models

import org.scalatest.Matchers
import org.scalatest.WordSpec

class TagContentSpec extends WordSpec with Matchers with TestData {

  "TagReplacement.replace" should {
    "replace repeated tags in text" in {
      val tag = "{firstname}"
      val str = tag + " is the worst bowler. " + tag + " really sucks."
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replace(str) shouldBe """Kip is the worst bowler. Kip really sucks."""
    }
  }
  
  "TagReplacement.replaceWithTooltip" should {
    "replace repeated tags in text" in {
      val tag = "{firstname}"
      val str = tag + " is the worst bowler. " + tag + " really sucks."
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replaceWithTooltip(str) shouldBe """<span class="article-tag" data-toggle="tooltip" data-placement="top">Kip</span> is the worst bowler. <span class="article-tag" data-toggle="tooltip" data-placement="top">Kip</span> really sucks."""
    }
  }
  
  "TagReplacement.replaceWithInlineEdit" should {
    "replace unset tag in inline edit HTML" in {
      val tag = "{first}"
      val str = """<a href="#" class="article-tag-editable" data-type="text" data-name="{first}">first</a> found in dumpster"""
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replaceWithInlineEdit(str) shouldBe """<a href="#" class="article-tag-editable" data-type="text" data-name="{first}">Kip</a> found in dumpster"""
    }
  }
  
  "TagContent.inlineEditHtml" should {
    "Wrap tags with inline edit HTML" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val contentForEditable = TagContent.inlineEditHtml(content, Set())
      contentForEditable shouldBe """Local Company declares <a href="#" class="article-tag-editable" data-type="text" data-name="{firstname}">firstname</a>&nbsp;<a href="#" class="article-tag-editable" data-type="text" data-name="{lastname}">lastname</a> Most Worthless Employee"""
    }
    
    "Wrap tags with inline edit HTML & replace set values" in {
      val content = "Local Company declares {firstname} {lastname} Most Worthless Employee"
      val tagReplacementSet = Set(TagReplacement("{firstname}", "Kip"))
      val contentForEditable = TagContent.inlineEditHtml(content, tagReplacementSet)
      contentForEditable shouldBe """Local Company declares <a href="#" class="article-tag-editable" data-type="text" data-name="{firstname}">Kip</a>&nbsp;<a href="#" class="article-tag-editable" data-type="text" data-name="{lastname}">lastname</a> Most Worthless Employee"""
    }
  }

}
