package models.slick

import javax.inject._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Content
import kipsigman.domain.entity.IdEntity
import kipsigman.domain.entity.Page
import kipsigman.domain.entity.PageFilter
import kipsigman.domain.entity.Status
import kipsigman.play.auth.entity.User
import models._

@Singleton()
class ModelRepositorySlick @Inject() (
  dbConfigProvider: DatabaseConfigProvider)
  (implicit protected val ec: ExecutionContext) extends ModelRepository with ModelDBConfig {
  
  import driver.api._
  
  override protected val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  
  private def articleResults(q: Query[ArticleTable, ArticleRow, Seq]): Future[Seq[Article]] = {
    db.run(q.withArticleTemplate.result).map(joinSeq =>
      joinSeq.map(join => join._1.toEntity(join._2))
    )
  }
  
  override def findArticle(id: Int): Future[Option[Article]] = {
    val q = articleTableQuery.filter(_.id === id)
    articleResults(q).map(_.headOption)
  }
  
  override def findArticlesByUser(user: User): Future[Seq[Article]] = {
    val q = articleTableQuery.filter(_.userIdOption === user.id).sortBy(_.id.asc)
    articleResults(q).map(_.filter(a => a.status != Status.Deleted))
  }
  
  override def findPublishedArticlesByCategory(category: Category, pageFilter: PageFilter)(implicit userOption: Option[User]): Future[Page[Article]] = {
    val q = articleTableQuery
    val itemsFuture = articleResults(q).map(entities =>
      entities.filter(ai =>
        ai.categories.contains(category) && (ai.isPublic || (ai.isUnlisted && ai.isOwnedBy(userOption)))
      )
    )
    
    pageResults(itemsFuture, pageFilter)
  }
  
  override def saveArticle(article: Article)(implicit userOption: Option[User]): Future[Article] = {
    val entity = new ArticleRow(article)
    if (entity.isPersisted) {
      db.run(articleTableQuery.insertOrUpdate(entity)).map(_ => article)
    } else {
      db.run((articleTableQuery returning articleTableQuery.map(_.id)) += entity).map(id =>
        article.copy(id = Some(id))
      )
    }
  }
  
  override def addTagReplacement(id: Int, tagReplacement: TagReplacement)(implicit userOption: Option[User]): Future[Article] = {
    findArticle(id) flatMap {
      case Some(article) => {
        val updatedArticle = article.addTagReplacement(tagReplacement)
        saveArticle(updatedArticle)
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }
  
  override def updateArticleStatus(id: Int, status: Status)(implicit userOption: Option[User]): Future[Article] = {
    findArticle(id) flatMap {
      case Some(article) => {
        val updatedArticle = article.updateStatus(status)
        saveArticle(updatedArticle).map(savedArticle => {
          // TODO: Broadcast status change event
          savedArticle  
        })
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }
  
  override def findArticleTemplate(id: Int): Future[Option[ArticleTemplate]] = {
    db.run(articleTemplateTableQuery.filter(_.id === id).result).map(_.headOption)
  }
  
  override def findArticleTemplates(categoryOption: Option[Category] = None): Future[Seq[ArticleTemplate]] = {
    val q = articleTemplateTableQuery.sortBy(_.headline.asc)
    db.run(q.result).map(entities =>
      categoryOption match {
        case Some(category) => entities.filter(at => at.isMemberOf(category)) 
        case None => entities
      }  
    )
  }
  
  override def findPublishedArticleTemplatesByCategory(category: Category)(implicit userOption: Option[User]): Future[Seq[ArticleTemplate]] = {
    val q = articleTemplateTableQuery.sortBy(_.headline.asc)
    db.run(q.result).map(entities =>
      entities.filter(entity => 
        entity.categories.contains(category) && (entity.isPublic || (entity.isUnlisted && entity.isOwnedBy(userOption)))
      )  
    )
  }
  
  override def saveArticleTemplate(entity: ArticleTemplate)(implicit userOption: Option[User]): Future[ArticleTemplate] = {
    if (entity.isPersisted) {
      db.run(articleTemplateTableQuery.insertOrUpdate(entity)).map(_ => entity)
    } else {
      db.run((articleTemplateTableQuery returning articleTemplateTableQuery.map(_.id)) += entity).map(id =>
        entity.copy(id = Some(id))
      )
    }
  }

  override def updateArticleTemplateStatus(id: Int, status: Status)(implicit userOption: Option[User]): Future[ArticleTemplate] = {
    findArticleTemplate(id) flatMap {
      case Some(articleTemplate) => {
        val updatedArticleTemplate = articleTemplate.updateStatus(status)
        saveArticleTemplate(updatedArticleTemplate)
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }
  
}