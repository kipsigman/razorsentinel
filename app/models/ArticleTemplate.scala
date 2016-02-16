package models

import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User

import models.Content.Status

case class ArticleTemplate(
    id: Option[Int] = None,
    userId: Int,
    status: Status = Status.Draft,
    category: Category = NewsCategoryOptions.National,
    headline: String = "",
    body: String = "",
    imageFileName: Option[String] = None,
    imageCaption: Option[String] = None) extends ArticleContent[ArticleTemplate] {
  
  override lazy val userIdOption: Option[Int] = Option(userId)
  
  override lazy val canPublish: Boolean = true
  
  override protected def updateStatusCopy(newStatus: Status): ArticleTemplate = copy(status = newStatus)
  
  override lazy val imageSource: ArticleContent.ImageSource = ArticleContent.ImageSource.Template
  
  lazy val tags: Set[String] = TagContent.tags(headline) ++ TagContent.tags(body)
  
  lazy val tagsSorted: Seq[String] = tags.toSeq.sorted
}