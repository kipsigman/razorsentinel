package models

import java.time.LocalDateTime

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Content
import kipsigman.domain.entity.Content.ContentClass
import kipsigman.domain.entity.ContentImage
import kipsigman.domain.entity.Status
import services.StringService

case class Article(
    id: Option[Int] = None,
    userId: Option[Int],
    articleTemplate: ArticleTemplate,
    status: Status = Status.Draft,
    tagReplacements: Set[TagReplacement] = Set(),
    publishDate: Option[LocalDateTime] = None) extends ArticleContent[Article] {
  
  override lazy val userIdOption: Option[Int] = userId
  
  override protected def updateStatusCopy(newStatus: Status): Article = copy(status = newStatus)
  
  override lazy val categories: Seq[Category] = articleTemplate.categories
  
  override val contentClass: ContentClass = Article.contentClass
  
  def addTagReplacement(tagReplacement: TagReplacement): Article = {
    val newTagReplacements = tagReplacements.find(_.tag == tagReplacement.tag) match {
      case Some(existingTr) => tagReplacements - existingTr + tagReplacement
      case None => tagReplacements + tagReplacement
    }
     
    this.copy(tagReplacements = newTagReplacements)
  }
  
  override def headline: String = {
    tagReplacements.foldLeft(articleTemplate.headline)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  override def body: String = {
    tagReplacements.foldLeft(articleTemplate.body)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  def headlineWithTooltips: String = {
    tagReplacements.foldLeft(articleTemplate.headline)((str, tagReplacement) => {
      tagReplacement.replaceWithTooltip(str)
    })
  }
  
  def bodyWithTooltips: String = {
    tagReplacements.foldLeft(articleTemplate.body)((str, tagReplacement) => {
      tagReplacement.replaceWithTooltip(str)
    })
  }
  
  lazy val seoAlias: String = id match {
    case Some(id) => s"${StringService.formatSeo(headline)}-${id}" 
    case None => throw new IllegalArgumentException("Article has no set ID")
  }
  
  def headlineInlineEditHtml: String = {
    TagContent.inlineEditHtml(articleTemplate.headline, tagReplacements)
  }
  
  def bodyInlineEditHtml: String = {
    TagContent.inlineEditHtml(articleTemplate.body, tagReplacements)
  }
  
}

object Article {
  val contentClass: ContentClass = new ContentClass("Article")
}

case class ArticleWithImages(article: Article, contentImages: Seq[ContentImage])