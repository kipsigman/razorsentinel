package models

import kipsigman.domain.entity.Category
import models.Content.Status
import services.StringService

case class Article(
    id: Option[Int] = None,
    userIdOption: Option[Int],
    articleTemplateId: Int,
    status: Status = Status.Draft,
    tagReplacements: Set[TagReplacement] = Set()) extends Content[Article] {
  
  // Can't determine this without the ArticleTemplate, default to false
  override lazy val canPublish: Boolean = false
  
  override protected def updateStatusCopy(newStatus: Status): Article = copy(status = newStatus)
  
  def addTagReplacement(tagReplacement: TagReplacement): Article = {
    val newTagReplacements = tagReplacements.find(_.tag == tagReplacement.tag) match {
      case Some(existingTr) => tagReplacements - existingTr + tagReplacement
      case None => tagReplacements + tagReplacement
    }
     
    this.copy(tagReplacements = newTagReplacements)
  }
  
  //private def tagReplacementMap: Map[String, String] = tagReplacements.map(_.toKeyValue).toMap
}

case class ArticleInflated(article: Article, articleTemplate: ArticleTemplate) extends ArticleContent[ArticleInflated] {
  override val id = article.id
  override val userIdOption = article.userIdOption
  override val status = article.status
  
  override lazy val canPublish = article.tagReplacements.size == articleTemplate.tags.size
  
  override protected def updateStatusCopy(newStatus: Status): ArticleInflated = copy(article = article.copy(status = newStatus))
  
  override lazy val category: Category = articleTemplate.category
  
  /**
   * Returns headline with Tag replacements.
   */
  override def headline: String = {
    article.tagReplacements.foldLeft(articleTemplate.headline)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  /**
   * Returns body with Tag replacements.
   */
  override def body: String = {
    article.tagReplacements.foldLeft(articleTemplate.body)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  lazy val articleTemplateId = article.articleTemplateId
  
  lazy val seoAlias: String = article.id match {
    case Some(id) => s"${id}-${StringService.formatSeo(headline)}" 
    case None => throw new IllegalArgumentException("Article has no set ID")
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