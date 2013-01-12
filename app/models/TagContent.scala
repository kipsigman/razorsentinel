package models

object TagContent {
  val TagRegex = """\{([\w]*)\}""".r
  
  def contentHtmlInlineEdit(content: String): String = {
    TagContent.TagRegex.replaceAllIn(content, tagMatch => wrapTagForEdit(tagMatch))
  }
  
  private def wrapTagForEdit(tagMatch: scala.util.matching.Regex.Match) = {
    "<a href=\"#\" class=\"field-editable\" data-type=\"text\" data-name=\"" + tagMatch.toString + "\" data-value=\"" + tagMatch.group(1) + "\" data-placement=\"right\" data-inputclass=\"input-large\">" + tagMatch.toString + "</a>"
  }
}

case class TagReplacement(tag: String, replacement: String) {
  
  val regex = tag.replace("{", "\\{").replace("}", "\\}").r
  
  def replace(str: String): String = {
    regex.replaceAllIn(str, replacement)
  }
  
  override def toString: String = {
    tag + "=" + replacement
  }
}