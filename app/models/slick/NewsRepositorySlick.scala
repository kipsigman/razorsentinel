package models.slick

import java.sql.Timestamp
import javax.inject._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.Logger

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import models._
import models.auth.UnauthorizedOperationException
import models.auth.User

trait NewsDBConfig extends NewsTableDefinitions with HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig: DatabaseConfig[JdbcProfile]
}

@Singleton()
class NewsRepositorySlick @Inject() (
  dbConfigProvider: DatabaseConfigProvider)
  (implicit ec: ExecutionContext) extends NewsRepository with NewsDBConfig {
  
  import driver.api._
  
  protected val logger = Logger(getClass)
  
  override protected val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  
  override def deleteArticle(id: Int)(implicit userOption: Option[User]): Future[Option[Article]] = {
    findArticleById(id) flatMap {
      case Some(article) => {
        if (article.canEdit(userOption)) {
          val deletedArticle = article.delete
          saveArticle(deletedArticle).map(Option(_))
        } else {
          Future.failed(new UnauthorizedOperationException("User does not own this Article"))
        }
      }
      case None => Future.successful(None)
    }
  }
  
  override def findArticleById(id: Int): Future[Option[Article]] = {
    val query = for {
      article <- articleQuery.filter(_.id === id)
    } yield (article)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case ep =>
          Article(
            ep.id,
            ep.userId,
            ep.articleTemplateId,
            ep.status,
            ep.tagReplacements)
      }
    }
  }
  
  override def findArticleInflatedById(id: Int): Future[Option[ArticleInflated]] = {
    for {
      articleOption <- findArticleById(id)
      articleTemplateOption <- if (articleOption.isDefined) findArticleTemplateById(articleOption.get.articleTemplateId) else Future.successful(None)
    } yield {
      (articleOption, articleTemplateOption) match {
        case (Some(article), Some(articleTemplate)) => Option(ArticleInflated(article, articleTemplate))
        case _ => None
      }
    }
  }
  
  override def findArticlesByUser(user: User): Future[Seq[ArticleInflated]] = {
    val filteredSortedArticleQuery = articleQuery.
      filter(_.userId === user.id).
      sortBy(_.id.asc)
    val q = for {
      a <- filteredSortedArticleQuery
      at <- articleTemplateQuery if a.articleTemplateId === at.id
    } yield (a, at)
    
    // TODO: Filter should be in query
    db.run(q.result).map(_.filter(ax => ax._1.status != ContentEntity.Status.Deleted).map(ax => ArticleInflated(ax._1, ax._2)))
  }
  
  override def saveArticle(entity: Article)(implicit userOption: Option[User]): Future[Article] = {
    if (entity.canEdit(userOption)) {
      if (entity.isPersisted) {
        db.run(articleQuery.filter(_.id === entity.id.get).update(entity)).map(_ => entity)
      } else {
        db.run((articleQuery returning articleQuery.map(_.id)) += entity).map(id => {
          val savedEntity = entity.copy(id = Some(id))
          logger.debug(s"saveArticle savedEntity=$savedEntity")
          savedEntity
        })
      }
    } else {
      Future.failed(new UnauthorizedOperationException("User does not own this Article"))
    }
  }
  
  override def addTagReplacement(id: Int, tagReplacement: TagReplacement)(implicit userOption: Option[User]): Future[ArticleInflated] = {
    findArticleInflatedById(id) flatMap {
      case Some(articleInflated) => {
        val updatedArticleInflated = articleInflated.addTagReplacement(tagReplacement)
        saveArticle(updatedArticleInflated.article).map(savedArticle =>
          updatedArticleInflated.copy(article = savedArticle)
        )
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }
  
  override def updateArticleStatus(id: Int, status: ContentEntity.Status)(implicit userOption: Option[User]): Future[ArticleInflated] = {
    findArticleInflatedById(id) flatMap {
      case Some(articleInflated) => {
        val updatedArticle = articleInflated.article.updateStatus(status)
        saveArticle(updatedArticle).map(savedArticle =>
          articleInflated.copy(article = savedArticle)
        )
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }

  override def findArticleTemplateById(id: Int): Future[Option[ArticleTemplate]] = {
    val query = for {
      articleTemplate <- articleTemplateQuery.filter(_.id === id)
    } yield (articleTemplate)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case e =>
          ArticleTemplate(
            e.id,
            e.categories,
            e.headline,
            e.body)
      }
    }
  }
  
  override def findArticleTemplates(categoryOption: Option[Category] = None): Future[Seq[ArticleTemplate]] = {
    val filteredQuery = articleTemplateQuery
    val sortedQuery = filteredQuery.sortBy(_.headline.asc)
    val all = db.run(sortedQuery.result)
    categoryOption match {
      case Some(category) => all.map(articleTemplates => articleTemplates.filter(at => at.hasCategory(category))) 
      case None => all
    }
  }

  override def saveArticleTemplate(entity: ArticleTemplate)(implicit userOption: Option[User]): Future[ArticleTemplate] = {
    if (entity.isPersisted) {
      db.run(articleTemplateQuery.filter(_.id === entity.id.get).update(entity)).map(_ => entity)
    } else {
      db.run((articleTemplateQuery returning articleQuery.map(_.id)) += entity).map(id => {
        val savedEntity = entity.copy(id = Some(id))
        savedEntity
      })
    }
  }

  private def pageQuery[T <: IdEntity](query: Query[IdTable[T], T, Seq], pageFilter: PageFilter): Future[Page[T]] = {

    // Query pageSize + 1 to determine if there are more results after this page
    val itemsQuery = query.drop(pageFilter.offset).take(pageFilter.pageSize + 1)
    val itemsFuture = db.run(itemsQuery.result)

    itemsFuture.map(items => {
      if (items.size > pageFilter.pageSize) {
        Page(items.dropRight(1), pageFilter, true)
      } else {
        Page(items, pageFilter, false)
      }
    })
  }

}
