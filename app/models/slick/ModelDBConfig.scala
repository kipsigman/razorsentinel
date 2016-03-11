package models.slick

import java.sql.Timestamp
import java.time.LocalDateTime

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.IdEntity
import kipsigman.domain.entity.Status
import kipsigman.domain.repository.slick.SlickRepository
import play.api.libs.json.Json

import models.Article
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
    
    def * = (id.?, userId, status, categories, headline, body) <> ((ArticleTemplate.apply _).tupled, ArticleTemplate.unapply)
  }
  
  val articleTemplateTableQuery = TableQuery[ArticleTemplateTable]
  
  
  case class ArticleRow(
    id: Option[Int],
    userId: Option[Int],
    articleTemplateId: Int,
    status: Status,
    tagReplacements: Set[TagReplacement],
    publishDate: Option[LocalDateTime]) extends IdEntity {
    
    def this(entity: Article) = this(entity.id, entity.userId, entity.articleTemplate.id.get, entity.status, entity.tagReplacements, entity.publishDate)
    
    def toEntity(articleTemplate: ArticleTemplate): Article = {
      Article(id, userId, articleTemplate, status, tagReplacements, publishDate) 
    }
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
    def publishDate = column[Option[LocalDateTime]]("publish_date")
    
    def * = (id.?, userIdOption, articleTemplateId, status, tagReplacements, publishDate) <> (ArticleRow.tupled, ArticleRow.unapply)
  }
  
  val articleTableQuery = TableQuery[ArticleTable]
  
  implicit class ArticleExtensions(q: driver.api.Query[ArticleTable, ArticleRow, Seq]) {
    // specify mapping of relationship to address
    def withArticleTemplate = q.join(articleTemplateTableQuery).on(_.articleTemplateId === _.id)
  }
  
}