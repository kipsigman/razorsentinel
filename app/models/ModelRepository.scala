package models

import scala.concurrent.Future

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Page
import kipsigman.domain.entity.PageFilter
import kipsigman.domain.entity.Status
import kipsigman.play.auth.entity.User

trait ModelRepository {
  
  def findArticle(id: Int): Future[Option[Article]]
  
  def findArticlesByUser(user: User): Future[Seq[Article]]
  
  def findPublishedArticlesByCategory(category: Category, pageFilter: PageFilter)(implicit userOption: Option[User]): Future[Page[Article]]

  def saveArticle(article: Article)(implicit userOption: Option[User]): Future[Article]

  def addTagReplacement(id: Int, tagReplacement: TagReplacement)(implicit userOption: Option[User]): Future[Article]
  
  def updateArticleStatus(id: Int, status: Status)(implicit userOption: Option[User]): Future[Article]

  def findArticleTemplate(id: Int): Future[Option[ArticleTemplate]]
  
  def findArticleTemplates(categoryOption: Option[Category] = None): Future[Seq[ArticleTemplate]]
  
  def findPublishedArticleTemplatesByCategory(category: Category)(implicit userOption: Option[User]): Future[Seq[ArticleTemplate]]

  def saveArticleTemplate(articleTemplate: ArticleTemplate)(implicit userOption: Option[User]): Future[ArticleTemplate]
  
  def updateArticleTemplateStatus(id: Int, status: Status)(implicit userOption: Option[User]): Future[ArticleTemplate]
}