package controllers

import javax.inject.{Inject, Singleton}

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models._
import services.NewsService
import util.Urls

@Singleton
class ArticleController @Inject() (newsService: NewsService) extends Controller {
  
  val updateTagForm: Form[Article] = Form[Article](
    mapping(
      "pk" -> longNumber,
      "articleTemplateId" -> longNumber,
      "name" -> text,
      "value" -> text
    )
    // Binding
    {(pk, articleTemplateId, name, value) => {
      val article = if (pk > 0) Article.findById(pk).get else Article(Entity.UnpersistedId, articleTemplateId, None, false)
      article.addTagReplacement(TagReplacement(name, value))
      }
    }
    // Unbinding
    {article => Some(article.id, article.articleTemplateId, "{tag}", "replace")}
  )
  
  def updateTag = Action.async { implicit request =>
    
    updateTagForm.bindFromRequest.fold(
      formWithErrors => scala.concurrent.Future(BadRequest("Bad data")),
      article => {
        val articleId = Article.save(article).id
        val savedArticle = Article.findByIdInflated(articleId)
        val articleUrlFuture = Article.preparedUrl(request, savedArticle)
        
        articleUrlFuture.map(url => {
          if (savedArticle.publish) {
            // All tags replaced, give URL for sharing
            val json = Json.toJson(Map("status" -> "PUBLISH", "url" -> url, "id" -> savedArticle.id.toString))
            Ok(json)
          } else {
            // Not completely customized
            val json = Json.toJson(Map("status" -> "DRAFT", "url" -> url, "articleId" -> savedArticle.id.toString))
            Ok(json)  
          }
        })
      }
    )
  }
  
  def listArticleTemplates = Action { implicit request =>
    val articleTemplates: List[ArticleTemplate] = ArticleTemplate.findAll
    Ok(views.html.article.listArticleTemplates(articleTemplates))
  }
  
  def create(articleTemplateId: Long) = Action { implicit request =>
    val articleTemplate = ArticleTemplate.findById(articleTemplateId).get
    val article = Article.save(Article(Entity.UnpersistedId, articleTemplate.id, None, false))
    Ok(views.html.article.create(article, articleTemplate))
  }
  
  def show(seoAlias: String) = Action { implicit request =>
    
    Article.findBySeoAlias(seoAlias).map(article => {
      val inflatedArticle = Article.findByIdInflated(article.id)
      Ok(views.html.article.show(inflatedArticle)) 
    }).getOrElse(NotFound)
  }
  
}