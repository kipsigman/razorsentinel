package models.slick

import javax.inject._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.IdEntity
import kipsigman.domain.repository.slick.SlickRepository
import kipsigman.play.auth.entity.User
import models._
import models.Content.Status

@Singleton()
class NewsRepositorySlick @Inject() (
  dbConfigProvider: DatabaseConfigProvider)
  (implicit protected val ec: ExecutionContext) extends NewsRepository with NewsDBConfig {
  
  import driver.api._
  
  override protected val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  
  override def findArticle(id: Int): Future[Option[Article]] = {
    val query = for {
      article <- articleQuery.filter(_.id === id)
    } yield (article)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case ep =>
          Article(
            ep.id,
            ep.userIdOption,
            ep.articleTemplateId,
            ep.status,
            ep.tagReplacements,
            ep.imageFileName)
      }
    }
  }
  
  override def findArticleInflated(id: Int): Future[Option[ArticleInflated]] = {
    for {
      articleOption <- findArticle(id)
      articleTemplateOption <- if (articleOption.isDefined) findArticleTemplate(articleOption.get.articleTemplateId) else Future.successful(None)
    } yield {
      (articleOption, articleTemplateOption) match {
        case (Some(article), Some(articleTemplate)) => Option(ArticleInflated(article, articleTemplate))
        case _ => None
      }
    }
  }
  
  override def findArticlesByUser(user: User): Future[Seq[ArticleInflated]] = {
    val filteredSortedArticleQuery = articleQuery.
      filter(_.userIdOption === user.id).
      sortBy(_.id.asc)
    val q = for {
      a <- filteredSortedArticleQuery
      at <- articleTemplateQuery if a.articleTemplateId === at.id
    } yield (a, at)
    
    // TODO: Filter should be in query
    db.run(q.result).map(_.filter(ax => ax._1.status != Status.Deleted).map(ax => ArticleInflated(ax._1, ax._2)))
  }
  
  override def findPublishedArticlesByCategory(category: Category)(implicit userOption: Option[User]): Future[Seq[ArticleInflated]] = {
    // TODO: Filter for Public & Unlisted
    val statusFilteredQuery = articleQuery
      
    val q = for {
      a <- statusFilteredQuery
      t <- articleTemplateQuery if a.articleTemplateId === t.id
    } yield (a, t)
    
    db.run(q.result).
      map(_.map(at => ArticleInflated(at._1, at._2)).
      filter(ai =>
        ai.category == category &&
          (ai.isPublic || (ai.isUnlisted && ai.isOwnedBy(userOption)))
        )
    )
  }
  
  override def saveArticle(article: Article)(implicit userOption: Option[User]): Future[Article] = {
    if (article.isPersisted) {
      db.run(articleQuery.filter(_.id === article.id.get).update(article)).map(_ => article)
    } else {
      db.run((articleQuery returning articleQuery.map(_.id)) += article).map(id => {
        val savedEntity = article.copy(id = Some(id))
        savedEntity
      })
    }
  }
  
  override def addTagReplacement(id: Int, tagReplacement: TagReplacement)(implicit userOption: Option[User]): Future[ArticleInflated] = {
    findArticleInflated(id) flatMap {
      case Some(articleInflated) => {
        val updatedArticleInflated = articleInflated.addTagReplacement(tagReplacement)
        saveArticle(updatedArticleInflated.article).map(savedArticle =>
          updatedArticleInflated.copy(article = savedArticle)
        )
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }
  
  override def updateArticleStatus(id: Int, status: Status)(implicit userOption: Option[User]): Future[ArticleInflated] = {
    findArticleInflated(id) flatMap {
      case Some(articleInflated) => {
        val updatedArticleInflated = articleInflated.updateStatus(status)
        saveArticle(updatedArticleInflated.article).map(savedArticle => {
          val returnEntity = updatedArticleInflated.copy(article = savedArticle)
          // If Article is Public, make Template Unlisted so both aren't indexed on site
          if (returnEntity.isPublic && returnEntity.articleTemplate.isPublic) {
            updateArticleTemplateStatus(returnEntity.articleTemplateId, Status.Unlisted)
          }
          returnEntity
        })
      }
      case None => Future.failed(new RuntimeException(s"Article $id not found"))
    }
  }

  override def findArticleTemplate(id: Int): Future[Option[ArticleTemplate]] = {
    val query = for {
      articleTemplate <- articleTemplateQuery.filter(_.id === id)
    } yield (articleTemplate)

    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case e =>
          ArticleTemplate(
            e.id,
            e.userId,
            e.status,
            e.category,
            e.headline,
            e.body,
            e.imageFileName,
            e.imageCaption)
      }
    }
  }
  
  override def findArticleTemplates(categoryOption: Option[Category] = None): Future[Seq[ArticleTemplate]] = {
    val filteredQuery = articleTemplateQuery
    val sortedQuery = filteredQuery.sortBy(_.headline.asc)
    val all = db.run(sortedQuery.result)
    categoryOption match {
      case Some(category) => all.map(articleTemplates => articleTemplates.filter(at => at.isMemberOf(category))) 
      case None => all
    }
  }
  
  override def findPublishedArticleTemplatesByCategory(category: Category)(implicit userOption: Option[User]): Future[Seq[ArticleTemplate]] = {
    // TODO: Filter for Public & Unlisted
    val statusFilteredQuery = articleTemplateQuery
      
    val q = for {
      t <- statusFilteredQuery
    } yield (t)
    
    db.run(q.result).
      map(_.filter(t =>
        t.category == category &&
          (t.isPublic || (t.isUnlisted && t.isOwnedBy(userOption)))
        )
    )
  }

  override def saveArticleTemplate(articleTemplate: ArticleTemplate)(implicit userOption: Option[User]): Future[ArticleTemplate] = {
    if (articleTemplate.isPersisted) {
      db.run(articleTemplateQuery.filter(_.id === articleTemplate.id.get).update(articleTemplate)).map(_ => articleTemplate)
    } else {
      db.run((articleTemplateQuery returning articleQuery.map(_.id)) += articleTemplate).map(id => {
        val savedEntity = articleTemplate.copy(id = Some(id))
        savedEntity
      })
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
