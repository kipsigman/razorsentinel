package models.slick

import play.api.libs.json._

import models._

trait NewsTableDefinitions extends TableDefinitions {
  import driver.api._
  
  class ArticleTemplateTable(tag: Tag) extends IdTable[ArticleTemplate](tag, "article_template") {
    implicit val categoryTypeMapper = MappedColumnType.base[Category, String](
      {category => category.name},
      {str => Category(str)}
    )
    implicit val categoriesTypeMapper = MappedColumnType.base[Set[Category], String](
      {categorySet => categorySet.map(_.name).mkString(",")},
      {str => 
        if (str.isEmpty())
          Set[Category]()
        else
          str.split(",").map(Category(_)).toSet
      }
    )
  
    def categories = column[Set[Category]]("categories")
    def headline = column[String]("headline")
    def body = column[String]("body")
    def * = (id.?, categories, headline, body) <> (ArticleTemplate.tupled, ArticleTemplate.unapply)
  }
  
  val articleTemplateQuery = TableQuery[ArticleTemplateTable]
  
  class ArticleTable(tag: Tag) extends IdTable[Article](tag, "article") {
    import models.ContentEntity.Status
    
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
    
    def userId = column[Option[Int]]("user_id")
    def articleTemplateId = column[Int]("article_template_id")
    def status = column[Status]("status")
    def tagReplacements = column[Set[TagReplacement]]("tag_replacements")
    
    def * = (id.?, userId, articleTemplateId, status, tagReplacements) <> (Article.tupled, Article.unapply)
    def articleTemplate = foreignKey("fk_article_article_template_id", articleTemplateId, articleTemplateQuery)(_.id)
  }
  
  val articleQuery = TableQuery[ArticleTable]
}