package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import models.Permission._

/**
 * Manage Article Templates
 */
object ArticleTemplateController extends SecureController { 
  
  def form(user: User) = Form[ArticleTemplate](
    mapping(
      "id" -> longNumber,
      "headline" -> nonEmptyText,
      "body" -> nonEmptyText
    )
    // Custom binding
    { (id, headline, body) =>
      {
        if (id > 0) {
          // Edit existing entity
          val articleTemplate = ArticleTemplate.findById(id).get
          articleTemplate.copy(headline = headline, body = body)
        } else {
          // New Entity
          ArticleTemplate(Entity.UnpersistedId, user.id, headline, body)
        }
      }
    }
    // Custom unbinding
    {articleTemplate => Some(articleTemplate.id, articleTemplate.headline, articleTemplate.body)}
  )
  
  def list = authorizedAction(Administrator) { implicit user => implicit request =>

  	val articleTemplates: List[ArticleTemplate] = ArticleTemplate.findAll
    Ok(views.html.articleTemplate.list(articleTemplates))
  }
  
  def create = authorizedAction(Administrator) { implicit user => implicit request =>
    val theForm = form(user).fill(new ArticleTemplate() )
    Ok(views.html.articleTemplate.edit(theForm))
  }

  def edit(id: Long) = authorizedAction(Administrator) { implicit user => implicit request =>

    ArticleTemplate.findById(id).map { articleTemplate =>
      val theForm = form(user).fill(articleTemplate)
      Ok(views.html.articleTemplate.edit(theForm))
    }.getOrElse(NotFound)
  }
  
  def show(id: Long) = authorizedAction(Administrator) { implicit user => implicit request =>
    ArticleTemplate.findById(id).map { articleTemplate =>
      Ok(views.html.articleTemplate.show(articleTemplate))
    }.getOrElse(NotFound)
  }
  
  def save = authorizedAction(Administrator) { implicit user => implicit request =>
    
    form(user).bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.articleTemplate.edit(formWithErrors)),
      articleTemplate => {
        val savedArticleTemplate = ArticleTemplate.save(articleTemplate)
        Redirect(routes.ArticleTemplateController.show(savedArticleTemplate.id)).flashing("success" -> "Article Template saved!")
      }
    )
  }
  
}
            