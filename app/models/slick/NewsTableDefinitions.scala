package models.slick

import models._

trait NewsTableDefinitions extends TableDefinitions {
  import driver.api._
  
  // News tables and queries
  class ArticleTable(tag: Tag) extends IdTable[Article](tag, "article") {
    def articleTemplateId = column[Int]("article_template_id")
    def tagReplacements = column[Option[String]]("tag_replacements")
    def publish = column[Boolean]("publish")
    def * = (id.?, articleTemplateId, tagReplacements, publish) <> (Article.tupled, Article.unapply)
  }
  
  class ArticleTemplateTable(tag: Tag) extends IdTable[ArticleTemplate](tag, "article_template") {
    def headline = column[String]("headline")
    def body = column[String]("body")
    def * = (id.?, headline, body) <> (ArticleTemplate.tupled, ArticleTemplate.unapply)
  }
  
  val articleQuery = TableQuery[ArticleTable]
  val articleTemplateQuery = TableQuery[ArticleTemplateTable]
}