package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.Table

/**
 * This object defines the ORM Schema for Squeryl.
 * @author kip
 */
object NewsSchema extends Schema {

  val userTable = table[User]("user")
  val articleTemplateTable = table[ArticleTemplate]("article_template")
  val articleTable = table[Article]("article")
  
  val userToArticleTemplates =
    oneToManyRelation(userTable, articleTemplateTable).
      via((u,at) => u.id === at.userId)
      
  val articleTemplateToArticles =
    oneToManyRelation(articleTemplateTable, articleTable).
      via((at,a) => at.id === a.articleTemplateId)
  
}