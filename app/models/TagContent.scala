package models

/**
 * Inline edit is done with X-editable: https://vitalets.github.io/x-editable/
 */
object TagContent {
  private val tagRegex = """\{([\w]*)\}""".r
  private val neighborTagsRegex = """\}([\s]+)\{""".r
  
  def tags(content: String): Set[String] = {
    tagRegex.findAllIn(content).toSet
  }
  
  def inlineEditHtml(content: String, tagReplacementSet: Set[TagReplacement]): String = {
    // X-editable seems to push neighboring tags with only whitespace separation next to each other when editing.
    // Replace with forced HTML space
    val forcedSpaceHtml = neighborTagsRegex.replaceAllIn(content, "}&nbsp;{")
      
    // Get edit HTML with no replacements
    val initialEditHtml = tagRegex.replaceAllIn(forcedSpaceHtml, tagMatch => inlineEditHtml(tagMatch))
    
    // Replace Default tag values with Replacements
    tagReplacementSet.foldLeft(initialEditHtml)((str, tagReplacement) => {
      tagReplacement.inlineEditReplace(str)
    })
  }

  private def inlineEditHtml(tagMatch: scala.util.matching.Regex.Match) = {
    "<a href=\"#\" class=\"field-editable\" data-type=\"text\" data-name=\"" + tagMatch.toString + "\" data-value=\"" + tagMatch.group(1) + "\">" + tagMatch.toString + "</a>"
  }
}

case class TagReplacement(tag: String, replacement: String) {
  
  // Value placeholder is tag without brackets
  val placeholderValue = tag.replace("{", "").replace("}", "")

  // First escape brackets so regex doesn't think they are special characters
  val tagRegexStr = tag.replace("{", "\\{").replace("}", "\\}")
  val tagRegex = tagRegexStr.r

  def replace(str: String): String = {
    tagRegex.replaceAllIn(str, replacement)
  }
  
  // Replace displayed value for Tag in edit HTML
  // <a href="#" class="field-editable" data-type="text" data-name="{first}" data-value="first">{first}</a>
  val inlineEditRegex = (s"""data-value="$placeholderValue">$tagRegexStr</a>""").r
  def inlineEditReplace(str: String): String = {
    val editReplacement = s"""data-value="$replacement">$replacement</a>"""
    inlineEditRegex.replaceAllIn(str, editReplacement)
  }
  
  def toKeyValue: (String, String) = tag -> replacement

  override def toString: String = {
    tag + "=" + replacement
  }
}