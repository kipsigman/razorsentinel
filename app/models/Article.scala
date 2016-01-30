package models

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import services.StringService

case class Article(
    id: Option[Int],
    userId: Option[Int],
    articleTemplateId: Int,
    tagReplacements: Option[String],
    publish: Boolean) extends IdEntity {

  def addTagReplacement(tagReplacement: TagReplacement): Article = {
    // Add tag replacement to map of existing
    val newTrm = tagReplacementMap + (tagReplacement.tag -> tagReplacement.replacement)

    // convert to JSON string
    val newTagReplacements = Json.toJson(newTrm).toString

    // Copy to this and create a new version
    this.copy(tagReplacements = Some(newTagReplacements))
  }

  def tagReplacementSet: Set[TagReplacement] = {
    val tagReplacementSeq = tagReplacementMap.map(item => TagReplacement(item._1, item._2)).toSeq

    //    val ordering = new Ordering[TagReplacement] { 
    //      def compare(x:TagReplacement,y:TagReplacement): Int = x.tag compare y.tag 
    //    }
    //    
    //    val aSet: SortedSet[TagReplacement] = SortedSet(tagReplacementList: _*)(ordering)
    //    aSet

    tagReplacementSeq.toSet
  }

  private def tagReplacementMap: Map[String, String] = {
    tagReplacements match {
      case None => Nil.toMap
      case Some(tagReplacementsDefined) => {
        val json: JsValue = Json.parse(tagReplacementsDefined)
        val map: Map[String, String] = json.as[Map[String, String]]
        map
      }
    }
  }

}

case class ArticleInflated(article: Article, articleTemplate: ArticleTemplate) {
  val id = article.id
  val userId = article.userId
  val publish = article.publish

  /**
   * Returns headline with all defined Tag replacements.
   */
  def headline: String = {
    article.tagReplacementSet.foldLeft(articleTemplate.headline)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  /**
   * Returns body with all defined Tag replacements.
   */
  def body: String = {
    article.tagReplacementSet.foldLeft(articleTemplate.body)((str, tagReplacement) => {
      tagReplacement.replace(str)
    })
  }
  
  /**
   * Returns headline for edit with all defined Tag replacements.
   */
  def headlineInlineEditHtml: String = {
    TagContent.inlineEditHtml(articleTemplate.headline, article.tagReplacementSet)
  }
  
  /**
   * Returns body for edit with all defined Tag replacements.
   */
  def bodyInlineEditHtml: String = {
    TagContent.inlineEditHtml(articleTemplate.body, article.tagReplacementSet)
  }

  def allTagsReplaced = {
    (article.tagReplacementSet.size == articleTemplate.tags.size)
  }

  def addTagReplacement(tagReplacement: TagReplacement): ArticleInflated = {
    val tagAddedArticle = article.addTagReplacement(tagReplacement)
    val tagAddedAi = this.copy(article = tagAddedArticle)

    // Set publish to true if all tags have been set
    val updatedArticle =
      if (tagAddedAi.allTagsReplaced)
        tagAddedArticle.copy(publish = true)
      else
        tagAddedArticle

    this.copy(article = updatedArticle)
  }
  
  lazy val seoAlias: String = s"${article.id.getOrElse(0)}-${StringService.formatSeo(headline)}"
  
  lazy val relativeUrl: String = {
    s"/articles/${seoAlias}" 
  }
}