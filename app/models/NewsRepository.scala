package models

import scala.concurrent.Future

import models.auth.User

trait NewsRepository {
  
  /**
   * Changes Article status to Deleted. Returns Article if successful,
   * None if not found, and UnauthorizedOperationException if user doesn't have permissions.
   */
  def deleteArticle(id: Int)(implicit userOption: Option[User]): Future[Option[Article]]

  def findArticleById(id: Int): Future[Option[Article]]
  
  def findArticleInflatedById(id: Int): Future[Option[ArticleInflated]]
  
  def findArticlesByUser(user: User): Future[Seq[ArticleInflated]]

  def saveArticle(entity: Article)(implicit userOption: Option[User]): Future[Article]

  def addTagReplacement(id: Int, tagReplacement: TagReplacement)(implicit userOption: Option[User]): Future[ArticleInflated]
  
  def updateArticleStatus(id: Int, status: ContentEntity.Status)(implicit userOption: Option[User]): Future[ArticleInflated]

  def findArticleTemplateById(id: Int): Future[Option[ArticleTemplate]]

  def findArticleTemplates(categoryOption: Option[Category] = None): Future[Seq[ArticleTemplate]]

  def saveArticleTemplate(entity: ArticleTemplate)(implicit userOption: Option[User]): Future[ArticleTemplate]
}

case class PageFilter(page: Int = 0, pageSize: Int = 20) {
  val offset = page * pageSize
}

case class Page[T](items: Seq[T], pageFilter: PageFilter, hasNext: Boolean) {
  lazy val prev = Option(pageFilter.page - 1).filter(_ >= 0)
  lazy val next = Option(pageFilter.page + 1).filter(_ => hasNext)
  lazy val from = pageFilter.offset + 1
  lazy val to = pageFilter.offset + items.size
}

