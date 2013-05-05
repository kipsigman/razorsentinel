package models

import org.squeryl.annotations.Column
import org.squeryl.dsl.ManyToOne

case class ArticleTemplate(
  id: Long, 
  headline: String,
  body: String) extends IdEntity {
  
  def this() = this(Entity.UnpersistedId, "", "")
    
  def tags: Set[String] = {
    collection.SortedSet.empty[String] ++ (TagContent.TagRegex.findAllIn(headline).toList ::: TagContent.TagRegex.findAllIn(body).toList)
  }
  
}

object ArticleTemplate extends Dao[ArticleTemplate](NewsSchema.articleTemplateTable)
