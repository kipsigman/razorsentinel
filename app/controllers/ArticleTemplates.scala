package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import views._
import models._
import jp.t2v.lab.play20.auth.Auth
import play.api.i18n.Messages

/**
 * Manage Article Templates
 */
object ArticleTemplates extends Controller with Auth with AuthConfigImpl { 
  
  /**
   * Describe the form (used in both edit and create screens).
   */ 
  val articleTemplateForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "headline" -> nonEmptyText,
      "body" -> nonEmptyText
    )(ArticleTemplate.apply)(ArticleTemplate.unapply)
  )
  
  // -- Actions
  def list = authorizedAction(Administrator) { user => implicit request =>
      val articleTemplates = ArticleTemplate.list
      Ok(views.html.articleTemplate.list(articleTemplates))
  }
  
  def edit(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    ArticleTemplate.findById(id).map { articleTemplate =>
      Ok(html.articleTemplate.edit(id, articleTemplateForm.fill(articleTemplate)))
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    articleTemplateForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.articleTemplate.edit(id, formWithErrors)),
      articleTemplate => {
        // println("articleTemplate.id=" + articleTemplate.id)
        ArticleTemplate.update(id, articleTemplate)
        Redirect(routes.ArticleTemplates.list()).flashing("success" -> Messages("articleTemplate.new.success"))
      }
    )
  }
  
  def create = authorizedAction(Administrator) { user => implicit request =>
    // val dummyArticleTemplate = ArticleTemplate(anorm.NotAssigned, "", "")
    Ok(html.articleTemplate.create(articleTemplateForm))
  }
  
  def save = authorizedAction(Administrator) { user => implicit request =>
    articleTemplateForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.articleTemplate.create(formWithErrors)),
      articleTemplate => {
        ArticleTemplate.insert(articleTemplate)
        Redirect(routes.ArticleTemplates.list()).flashing("success" -> Messages("articleTemplate.new.success"))
      }
    )
  }
  
  def delete(id: Long) = authorizedAction(Administrator) { user => implicit request =>
    ArticleTemplate.delete(id)
    Redirect(routes.ArticleTemplates.list()).flashing("success" -> Messages("articleTemplate.new.success"))
  }

}
            