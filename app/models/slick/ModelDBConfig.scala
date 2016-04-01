package models.slick

import java.sql.Timestamp
import java.time.LocalDateTime

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.IdEntity
import kipsigman.domain.entity.LifecycleEntity
import kipsigman.domain.entity.Status
import kipsigman.domain.repository.slick.SlickRepository
import kipsigman.play.auth.entity.User
import play.api.libs.json.Json

import models.Article
import models.ArticleComment
import models.ArticleTemplate
import models.NewsCategoryOptions
import models.TagReplacement

trait ModelDBConfig extends SlickRepository {
  import driver.api._
  
  class ArticleTemplateTable(tag: Tag) extends IdTable[ArticleTemplate](tag, "article_template") {
    implicit val statusTypeMapper = MappedColumnType.base[Status, String](
      {status => status.name},
      {str => Status.apply(str)}
    )
    
    implicit val categoryTypeMapper = MappedColumnType.base[Category, String](
      {category => category.name},
      {str => NewsCategoryOptions(str)}
    )
    
    implicit val categoriesTypeMapper = MappedColumnType.base[Seq[Category], String](
      {categorySet => categorySet.map(_.name).mkString(",")},
      {str => 
        if (str.isEmpty())
          Seq[Category]()
        else
          str.split(",").map(str => NewsCategoryOptions(str))
      }
    )
  
    def userId = column[Int]("user_id")
    def status = column[Status]("status")
    def categories = column[Seq[Category]]("categories")
    def headline = column[String]("headline")
    def body = column[String]("body")
    def author = column[String]("author")
    
    def * = (id.?, userId, status, categories, headline, body, author) <> ((ArticleTemplate.apply _).tupled, ArticleTemplate.unapply)
  }
  
  val articleTemplateTableQuery = TableQuery[ArticleTemplateTable]
  
  
  case class ArticleRow(
    id: Option[Int],
    userId: Option[Int],
    articleTemplateId: Int,
    status: Status,
    tagReplacements: Set[TagReplacement],
    author: Option[String],
    publishDate: Option[LocalDateTime]) extends LifecycleEntity[ArticleRow] {
    
    def this(entity: Article) = this(entity.id, entity.userId, entity.articleTemplate.id.get, entity.status, entity.tagReplacements, entity.author, entity.publishDate)
    
    def toEntity(articleTemplate: ArticleTemplate): Article = {
      Article(id, userId, articleTemplate, status, tagReplacements, author, publishDate) 
    }
    
    override protected def updateStatusCopy(newStatus: Status): ArticleRow = copy(status = newStatus)
  }
    
  class ArticleTable(tag: Tag) extends IdTable[ArticleRow](tag, "article") {
    implicit val statusTypeMapper = MappedColumnType.base[Status, String](
      {status => status.name},
      {str => Status.apply(str)}
    )
  
    implicit val tagReplacementsTypeMapper = MappedColumnType.base[Set[TagReplacement], String](
      tagReplacementSet => {
        val trMap: Map[String, String] = tagReplacementSet.map(_.toKeyValue).toMap
        val json = Json.toJson(trMap)
        Json.stringify(json)
      },
      str => { 
        if (str.isEmpty()) {
          Set[TagReplacement]()
        } else {
          val json = Json.parse(str)
          val map: Map[String, String] = json.as[Map[String, String]]
          val set = map.toSet
          set.map(kv => TagReplacement(kv._1, kv._2))
        }
      }
    )
    
    implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
      localDateTime => Timestamp.valueOf(localDateTime),
      timestamp => timestamp.toLocalDateTime()
    )
    
    def userIdOption = column[Option[Int]]("user_id")
    def articleTemplateId = column[Int]("article_template_id")
    def status = column[Status]("status")
    def tagReplacements = column[Set[TagReplacement]]("tag_replacements")
    def author = column[Option[String]]("author")
    def publishDate = column[Option[LocalDateTime]]("publish_date")
    
    def * = (id.?, userIdOption, articleTemplateId, status, tagReplacements, author, publishDate) <> (ArticleRow.tupled, ArticleRow.unapply)
  }
  
  val articleTableQuery = TableQuery[ArticleTable]
  
  implicit class ArticleExtensions(q: driver.api.Query[ArticleTable, ArticleRow, Seq]) {
    // specify mapping of relationship to address
    def withArticleTemplate = q.join(articleTemplateTableQuery).on(_.articleTemplateId === _.id)
  }
  
  case class ArticleCommentRow(
    id: Option[Int],
    articleId: Int,
    parentId: Option[Int],
    userId: Int,
    createDateTime: LocalDateTime,
    body: String) extends IdEntity {
    
    def this(entity: ArticleComment) = 
      this(entity.id, entity.articleId, entity.parentId, entity.user.id.get, entity.createDateTime, entity.body)
    
    def toEntity(user: User): ArticleComment = {
      ArticleComment(id, articleId, parentId, user, createDateTime, body) 
    }
  }
  
  class ArticleCommentTable(tag: Tag) extends IdTable[ArticleCommentRow](tag, "article_comment") {
    implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
      localDateTime => Timestamp.valueOf(localDateTime),
      timestamp => timestamp.toLocalDateTime()
    )
    
    def articleId = column[Int]("article_id")
    def parentId = column[Option[Int]]("parent_id")
    def userId = column[Int]("user_id")
    def createDateTime = column[LocalDateTime]("create_date_time")
    def body = column[String]("body")
    
    def * = (id.?, articleId, parentId, userId, createDateTime, body) <> (ArticleCommentRow.tupled, ArticleCommentRow.unapply)
  }
  
  val articleCommentTableQuery = TableQuery[ArticleCommentTable]
  
}