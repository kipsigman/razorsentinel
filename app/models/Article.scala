package models

import play.api.libs.json._
import play.api.mvc.{AnyContent,Request}
import org.squeryl.annotations.Column
import org.squeryl.annotations.Transient
import org.squeryl.dsl.StatefulManyToOne
import org.squeryl.PrimitiveTypeMode.inTransaction
import util.{Strings,Urls}

case class Article(
  id: Long,
  @Column("article_template_id")
  articleTemplateId: Long,
  @Column("tag_replacements")
  tagReplacements: Option[String]) extends IdEntity {
  
  lazy val articleTemplate: StatefulManyToOne[ArticleTemplate] = 
    NewsSchema.articleTemplateToArticles.rightStateful(this)
  
  def addTagReplacement(tagReplacement: TagReplacement): Article = {
    // Add tag replacement to map of existing
    val newTrm = tagReplacementMap + (tagReplacement.tag -> tagReplacement.replacement)
    
    // convert to JSON string
    val newTagReplacements = Json.toJson(newTrm).toString
    
    // Copy to this and create a new version
    this.copy(tagReplacements = Some(newTagReplacements))
  }
  
  private def tagReplacementMap: Map[String,String] = {
    tagReplacements match {
      case None => Nil.toMap
      case Some(tagReplacementsDefined) => {
        val json: JsValue = Json.parse(tagReplacementsDefined)
        val map: Map[String,String] = json.as[Map[String,String]]
        map
      }
    }
  }
  
  def tagReplacementSet: Set[TagReplacement] = {
    tagReplacementMap.map(item => TagReplacement(item._1, item._2)).toSet
  }
  
  def body: String = {
    tagReplacementSet.foldLeft(articleTemplate.one.get.body)((str, tagReplacement) => {
       tagReplacement.replace(str)
    })
  }
  
  def headline: String = {
    tagReplacementSet.foldLeft(articleTemplate.one.get.headline)((str, tagReplacement) => {
       tagReplacement.replace(str)
    })
  }
  
  def isPublished = {
    (tagReplacementSet.size == articleTemplate.one.get.tags.size)
  }
  
  def relativeUrl: String = {
    Article.UrlPrefix + Strings.formatSeo(headline) + "-" + id
  }
  
}

object Article extends Dao(NewsSchema.articleTable) {
  
  val UrlPrefix = "/articles/"
    
  def findBySeoAlias(seoAlias: String): Option[Article] = inTransaction {
    
    // /article/some-seo-alias-path-with-an-id-at-the-end-99
    val lastDash = seoAlias.lastIndexOf('-')
    try {
      val id = seoAlias.substring(lastDash+1).toLong
      findById(id)  
    } catch {
      case _ => None
    }
  }
  
  def findByIdInflated(id: Long): Article = inTransaction {
    val article = findById(id).get
    article.articleTemplate.one.get
    article
  }
  
  def absoluteUrl(request: Request[AnyContent], article: Article): String = inTransaction {
    Urls.absoluteUrl(request, article.relativeUrl)
  }
}