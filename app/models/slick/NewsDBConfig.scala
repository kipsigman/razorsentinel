package models.slick

import play.api.libs.json._

import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.User
import kipsigman.domain.repository.slick.SlickRepository

import models._
import models.ContentEntity.Status

trait NewsDBConfig extends SlickRepository {
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
    
//    // Use for secondary categories if that feature is added
//    implicit val categoriesTypeMapper = MappedColumnType.base[Set[Category], String](
//      {categorySet => categorySet.map(_.name).mkString(",")},
//      {str => 
//        if (str.isEmpty())
//          Set[Category]()
//        else
//          str.split(",").map(Category(_)).toSet
//      }
//    )
  
    def userId = column[Int]("user_id")
    def status = column[Status]("status")
    def category = column[Category]("category")
    def headline = column[String]("headline")
    def body = column[String]("body")
    def * = (id.?, userId, status, category, headline, body) <> (ArticleTemplate.tupled, ArticleTemplate.unapply)
  }
  
  val articleTemplateQuery = TableQuery[ArticleTemplateTable]
  
  
  class ArticleTable(tag: Tag) extends IdTable[Article](tag, "article") {

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
    
    def userId = column[Int]("user_id")
    def articleTemplateId = column[Int]("article_template_id")
    def status = column[Status]("status")
    def tagReplacements = column[Set[TagReplacement]]("tag_replacements")
    
    def * = (id.?, userId, articleTemplateId, status, tagReplacements) <> (Article.tupled, Article.unapply)
    def articleTemplate = foreignKey("fk_article_article_template_id", articleTemplateId, articleTemplateQuery)(_.id)
  }
  
  val articleQuery = TableQuery[ArticleTable]
}