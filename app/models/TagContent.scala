package models

object TagContent {
  val TagRegex = """\{([\w]*)\}""".r

  def inlineEditHtml(content: String, tagReplacementSet: Set[TagReplacement]): String = {
    // Get edit HTML with no replacements
    val initialEditHtml = TagContent.TagRegex.replaceAllIn(content, tagMatch => inlineEditHtml(tagMatch))
    
    // Replace set tags
    tagReplacementSet.foldLeft(initialEditHtml)((str, tagReplacement) => {
      tagReplacement.inlineEditReplace(str)
    })
  }

  private def inlineEditHtml(tagMatch: scala.util.matching.Regex.Match) = {
    "<a href=\"#\" class=\"field-editable\" data-type=\"text\" data-name=\"" + tagMatch.toString + "\" data-value=\"" + tagMatch.group(1) + "\">" + tagMatch.toString + "</a>"
  }
}

case class TagReplacement(tag: String, replacement: String) {

  // First escape brackets so regex doesn't think they are special characters
  val regex = tag.replace("{", "\\{").replace("}", "\\}").r

  def replace(str: String): String = {
    regex.replaceAllIn(str, replacement)
  }
  
  // Replace displayed value for Tag in edit HTML
  // <a href="#" class="field-editable" data-type="text" data-name="{first}" data-value="first">{first}</a>
  val inlineEditRegex = (">" + tag.replace("{", "\\{").replace("}", "\\}") + "</a>").r
  def inlineEditReplace(str: String): String = {
    val editReplacement = s">$replacement</a>"
    inlineEditRegex.replaceAllIn(str, editReplacement)
  }

  override def toString: String = {
    tag + "=" + replacement
  }
}