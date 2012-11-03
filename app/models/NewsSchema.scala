package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.Table

/**
 * This object defines the ORM Schema for Squeryl.
 * @author kip
 */
object NewsSchema extends Schema {

  val articleTemplateTable = table[ArticleTemplate]("article_template")
  
  on(articleTemplateTable) { t =>
    declare {
      t.id is (autoIncremented)
    }
  }
}