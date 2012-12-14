package models

import org.squeryl.annotations.Column
import org.squeryl.dsl.ManyToOne

case class ArticleTemplate(
  id: Long, 
  @Column("user_id")
  userId: Long,
  headline: String,
  body: String) extends IdEntity with TagContent {
  
  def this() = this(Entity.UnpersistedId, Entity.UnpersistedId, "", "")
  
  lazy val user: ManyToOne[User] = 
    NewsSchema.userToArticleTemplates.right(this)
  
  def headlineEdit: String = {
    TagRegex.replaceAllIn(headline, tagMatch => wrapTagForEdit(tagMatch))
  }
  
  def bodyEdit: String = {
    TagRegex.replaceAllIn(body, tagMatch => wrapTagForEdit(tagMatch))
  }
  
  private def wrapTagForEdit(tagMatch: scala.util.matching.Regex.Match) = {
    "<a href=\"#\" class=\"field-editable\" data-type=\"text\" data-name=\"" + tagMatch.toString + "\" data-value=\"" + tagMatch.group(1) + "\">" + tagMatch.toString + "</a>"
  }
}

object ArticleTemplate extends Dao(NewsSchema.articleTemplateTable)
