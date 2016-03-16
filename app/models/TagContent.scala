package models

import play.api.i18n.Messages

/**
 * Inline edit is done with X-editable: https://vitalets.github.io/x-editable/
 */
object TagContent {
  private val tagRegex = """\{([\w\s/-]*)\}""".r
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
      tagReplacement.replaceWithInlineEdit(str)
    })
  }

  private def inlineEditHtml(tagMatch: scala.util.matching.Regex.Match) = {
    s"""<a href="#" class="article-tag-editable" data-type="text" data-name="${tagMatch.toString}">${tagMatch.group(1)}</a>"""
  }
}

case class TagReplacement(tag: String, replacement: String) {
  
  // Value placeholder is tag without brackets
  val placeholderValue = tag.replace("{", "").replace("}", "")

  // First escape brackets so regex doesn't think they are special characters
  val tagRegexStr = tag.replace("{", "\\{").replace("}", "\\}")
  val tagRegex = tagRegexStr.r

  def replace(str: String): String = {
    val replaceHtml = s"""${replacement}"""
    tagRegex.replaceAllIn(str, replaceHtml)
  }
  
  def replaceWithTooltip(str: String): String = {
    val replaceHtml = s"""<span class="article-tag" data-toggle="tooltip" data-placement="top">${replacement}</span>"""
    tagRegex.replaceAllIn(str, replaceHtml)
  }
  
  // Replace displayed value for Tag in edit HTML
  // <a href="#" class="field-editable" data-type="text" data-name="{first}">first</a>
  val inlineEditRegex = (s""">$placeholderValue</a>""").r
  def replaceWithInlineEdit(str: String): String = {
    val editReplacement = s""">$replacement</a>"""
    inlineEditRegex.replaceAllIn(str, editReplacement)
  }
  
  def toKeyValue: (String, String) = tag -> replacement

  override def toString: String = {
    tag + "=" + replacement
  }
}