package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

/**
 * Manage Article Templates
 */
object ArticleTemplateController extends Controller with Secured { 
  
  val form: Form[ArticleTemplate] = Form(
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
          ArticleTemplate(Entity.UnpersistedId, headline, body)
        }

      }
    }
    // Custom unbinding
    {ArticleTemplate.unapply}
  )
  
  def list = withUser { implicit user => implicit request =>

  	val articleTemplates: List[ArticleTemplate] = ArticleTemplate.findAll
    Ok(views.html.articleTemplate.list(articleTemplates))
  }
  
  def create = withUser { implicit user => implicit request =>
    val theForm = form.fill(ArticleTemplate(Entity.UnpersistedId, "", "") )
    Ok(views.html.articleTemplate.edit(theForm))
  }

  def edit(id: Long) = withUser { implicit user => implicit request =>

    ArticleTemplate.findById(id).map { articleTemplate =>
      val theForm = form.fill(articleTemplate)
      Ok(views.html.articleTemplate.edit(theForm))
    }.getOrElse(NotFound)
  }
  
  def save = withUser { implicit user => implicit request =>
    
    form.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.articleTemplate.edit(formWithErrors)),
      articleTemplate => {
        ArticleTemplate.save(articleTemplate)
        Redirect(routes.ArticleTemplateController.list()).flashing("success" -> "Article Template saved!")
      }
    )
  }
  
}
            