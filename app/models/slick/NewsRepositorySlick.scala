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
import models.auth.User

trait NewsDBConfig extends NewsTableDefinitions with HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig: DatabaseConfig[JdbcProfile]
}

@Singleton()
class NewsRepositorySlick @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends NewsRepository with NewsDBConfig {
  import driver.api._
  
  protected val logger = Logger(getClass)
  
  override protected val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  
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
            ep.tagReplacements,
            ep.publish)
      }
    }
  }
  
  override def findArticleBySeoAlias(seoAlias: String): Future[Option[Article]] = {
    // /article/99-some-seo-alias-path-prefixed-with-an-id
    val firstDash = seoAlias.indexOf('-')
    try {
      val id: Int = firstDash match {
        case -1 => seoAlias.toInt
        case x if(x > 0) => seoAlias.substring(0, firstDash).toInt
        case x => throw new IllegalArgumentException(s"$seoAlias is an invalid article path")
      }
      findArticleById(id)  
    } catch {
      case t : Throwable => Future.failed(t)
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
    val filteredSortedArticleQuery = articleQuery.filter(_.userId === user.id).sortBy(_.id.asc)
    val q = for {
      a <- filteredSortedArticleQuery
      at <- articleTemplateQuery if a.articleTemplateId === at.id
    } yield (a, at)
    
    db.run(q.result).map(_.map(ax => ArticleInflated(ax._1, ax._2)))
  }

  override def saveArticle(entity: Article): Future[Article] = {
    logger.debug(s"saveArticle $entity")
    if (entity.id.isDefined) {
      db.run(articleQuery.filter(_.id === entity.id.get).update(entity)).map(_ => entity)
    } else {
      db.run((articleQuery returning articleQuery.map(_.id)) += entity).map(id => {
        val savedEntity = entity.copy(id = Some(id))
        logger.debug(s"saveArticle savedEntity=$savedEntity")
        savedEntity
      })
    }
  }
  
  override def addTagReplacement(articleId: Int, tagReplacement: TagReplacement): Future[ArticleInflated] = {
    findArticleInflatedById(articleId) flatMap {
      case Some(articleInflated) => {
        val updatedArticleInflated = articleInflated.addTagReplacement(tagReplacement)
        saveArticle(updatedArticleInflated.article).map(savedArticle =>
          updatedArticleInflated.copy(article = savedArticle)
        )
      }
      case None => Future.failed(new RuntimeException(s"Article $articleId not found"))
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
            e.headline,
            e.body)
      }
    }
  }
  
  override def findArticleTemplates: Future[Seq[ArticleTemplate]] = {
    val filteredQuery = articleTemplateQuery
    val sortedQuery = filteredQuery.sortBy(_.headline.asc)
    db.run(sortedQuery.result)
  }

  override def saveArticleTemplate(entity: ArticleTemplate): Future[ArticleTemplate] = {
    if (entity.id.isDefined) {
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
