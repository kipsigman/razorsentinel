package models

import org.squeryl.annotations.Column

case class ArticleTemplate(id: Long, headline: String, body: String) extends IdEntity

object ArticleTemplate extends Dao(NewsSchema.articleTemplateTable) {


}
