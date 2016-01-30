package models.slick

import models._

trait NewsTableDefinitions extends TableDefinitions {
  import driver.api._
  
  class ArticleTemplateTable(tag: Tag) extends IdTable[ArticleTemplate](tag, "article_template") {
    def headline = column[String]("headline")
    def body = column[String]("body")
    def * = (id.?, headline, body) <> (ArticleTemplate.tupled, ArticleTemplate.unapply)
  }
  
  val articleTemplateQuery = TableQuery[ArticleTemplateTable]
  
  class ArticleTable(tag: Tag) extends IdTable[Article](tag, "article") {
    def userId = column[Option[Int]]("user_id")
    def articleTemplateId = column[Int]("article_template_id")
    def tagReplacements = column[Option[String]]("tag_replacements")
    def publish = column[Boolean]("publish")
    def * = (id.?, userId, articleTemplateId, tagReplacements, publish) <> (Article.tupled, Article.unapply)
    def articleTemplate = foreignKey("fk_article_article_template_id", articleTemplateId, articleTemplateQuery)(_.id)
  }
  
  val articleQuery = TableQuery[ArticleTable]
}