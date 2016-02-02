package models

import models.ContentEntity.Status
import models.auth.User
import services.StringService

case class Article(
    id: Option[Int] = None,
    userId: Option[Int] = None,
    articleTemplateId: Int,
    status: Status = Status.Draft,
    tagReplacements: Set[TagReplacement] = Set()) extends ContentEntity {
  
  // Can't determine this without the ArticleTemplate, default to false
  override val canPublish: Boolean = false
  
  ///////////
  // State Changes: TODO: Put into ContentEntity
  ///////////
  def delete: Article = this.copy(status = Status.Deleted)
  
  def publishPublic: Article = {
    assert(status != Status.Deleted, s"${Status.Deleted} -> ${Status.Public} is an invalid state change")
    this.copy(status = Status.Public)
  }
  
  def publishUnlisted: Article = {
    assert(status != Status.Deleted, s"${Status.Deleted} -> ${Status.Unlisted} is an invalid state change")
    this.copy(status = Status.Unlisted)
  }
  
  def revertToDraft: Article = {
    assert(Status.publishValues.contains(status), s"$status -> ${Status.Draft} is an invalid state change")
    this.copy(status = Status.Draft)
  }
  
  //  TODO: Check for valid status change
  def updateStatus(newStatus: Status): Article = this.copy(status = newStatus) 
  ///////////
  // End State Changes
  ///////////

  def addTagReplacement(tagReplacement: TagReplacement): Article = {
    val newTagReplacements = tagReplacements.find(_.tag == tagReplacement.tag) match {
      case Some(existingTr) => tagReplacements - existingTr + tagReplacement
      case None => tagReplacements + tagReplacement
    }
     
    this.copy(tagReplacements = newTagReplacements)
  }
  
  private def tagReplacementMap: Map[String, String] = tagReplacements.map(_.toKeyValue).toMap
}

case class ArticleInflated(article: Article, articleTemplate: ArticleTemplate) extends ContentEntity with CategorizedEntity {
  override val id = article.id
  override val userId = article.userId
  override val status = article.status
  
  override val canPublish = article.tagReplacements.size == articleTemplate.tags.size
  
  override def categories: Set[Category] = articleTemplate.categories
  
  lazy val seoAlias: String = article.id match {
    case Some(id) => s"${id}-${StringService.formatSeo(headline)}" 
    case None => throw new IllegalArgumentException("Article has no set ID")
  }
  
  lazy val relativeUrl: String = s"/articles/${seoAlias}" 
  
  /**
   * Returns headline with Tag replacements.
   */
  def headline: String = {
    article.tagReplacements.foldLeft(articleTemplate.headline)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  /**
   * Returns body with Tag replacements.
   */
  def body: String = {
    article.tagReplacements.foldLeft(articleTemplate.body)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  /**
   * Returns headline for inline edit.
   */
  def headlineInlineEditHtml: String = {
    TagContent.inlineEditHtml(articleTemplate.headline, article.tagReplacements)
  }
  
  /**
   * Returns body for inline edit.
   */
  def bodyInlineEditHtml: String = {
    TagContent.inlineEditHtml(articleTemplate.body, article.tagReplacements)
  }

  def addTagReplacement(tagReplacement: TagReplacement): ArticleInflated = {
    val updatedArticle = article.addTagReplacement(tagReplacement)
    this.copy(article = updatedArticle)
  }
}