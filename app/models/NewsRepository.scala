package models

import scala.concurrent.Future

import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.User

trait NewsRepository extends ContentRepository {
  
  def findArticle(id: Int): Future[Option[Article]]
  
  def findArticleInflated(id: Int): Future[Option[ArticleInflated]]
  
  def findArticleForEdit(id: Int)(implicit userOption: Option[User]): Future[Option[ArticleInflated]]
  
  def findArticlesByUser(user: User): Future[Seq[ArticleInflated]]
  
  def findPublishedArticlesByCategory(category: Category)(implicit userOption: Option[User]): Future[Seq[ArticleInflated]]

  def saveArticle(article: Article)(implicit userOption: Option[User]): Future[Article]

  def addTagReplacement(id: Int, tagReplacement: TagReplacement)(implicit userOption: Option[User]): Future[ArticleInflated]
  
  def updateArticleStatus(id: Int, status: ContentEntity.Status)(implicit userOption: Option[User]): Future[ArticleInflated]

  def findArticleTemplate(id: Int): Future[Option[ArticleTemplate]]
  
  def findArticleTemplateForEdit(id: Int)(implicit userOption: Option[User]): Future[Option[ArticleTemplate]]

  def findArticleTemplates(categoryOption: Option[Category] = None): Future[Seq[ArticleTemplate]]
  
  def findPublishedArticleTemplatesByCategory(category: Category)(implicit userOption: Option[User]): Future[Seq[ArticleTemplate]]

  def saveArticleTemplate(articleTemplate: ArticleTemplate)(implicit userOption: Option[User]): Future[ArticleTemplate]
  
  def updateArticleTemplateStatus(id: Int, status: ContentEntity.Status)(implicit userOption: Option[User]): Future[ArticleTemplate]
}