package models

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Content.ContentClass
import kipsigman.domain.entity.ContentImage
import kipsigman.domain.entity.Role
import kipsigman.domain.entity.Status
import kipsigman.play.auth.entity.User

case class ArticleTemplate(
    id: Option[Int] = None,
    userId: Int,
    status: Status = Status.Draft,
    categories: Seq[Category] = Seq(NewsCategoryOptions.National),
    headline: String = "",
    body: String = "",
    author: String = "") extends ArticleContent[ArticleTemplate] {
  
  override lazy val userIdOption: Option[Int] = Option(userId)
  
  override protected def updateStatusCopy(newStatus: Status): ArticleTemplate = copy(status = newStatus)
  
  override val contentClass: ContentClass = ArticleTemplate.contentClass
  
  lazy val tags: Set[String] = TagContent.tags(headline) ++ TagContent.tags(body)
  
  lazy val tagsSorted: Seq[String] = tags.toSeq.sorted
}

object ArticleTemplate {
  val contentClass: ContentClass = new ContentClass("ArticleTemplate")
}

case class ArticleTemplateWithImages(articleTemplate: ArticleTemplate, contentImages: Seq[ContentImage])