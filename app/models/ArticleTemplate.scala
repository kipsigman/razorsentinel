package models

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.CategorizedEntity
import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User

import models.ContentEntity.Status

trait ArticleContent[T <: ArticleContent[T]] extends ContentEntity[T] with CategorizedEntity {
  def headline: String
  def body: String
}

case class ArticleTemplate(
    id: Option[Int] = None,
    userId: Int,
    status: Status = Status.Draft,
    category: Category = NewsCategoryOptions.National,
    headline: String = "",
    body: String = "") extends ArticleContent[ArticleTemplate] {
  
  override def canEdit(userOption: Option[User]): Boolean = {
    userOption.map(user => user.isEditor && super.canEdit(userOption)).getOrElse(false)
  }
  
  override val canPublish: Boolean = true
  
  override protected def updateStatusCopy(newStatus: Status): ArticleTemplate = copy(status = newStatus)
  
  lazy val tags: Set[String] = TagContent.tags(headline) ++ TagContent.tags(body)
  
  lazy val tagsSorted: Seq[String] = tags.toSeq.sorted
}