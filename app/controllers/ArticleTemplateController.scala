package controllers

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.optional
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.Action

import models.ArticleTemplate
import models.NewsRepository
import models.auth.Role
import models.auth.User

@Singleton
class ArticleTemplateController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  newsRepository: NewsRepository)(implicit ec: ExecutionContext) extends BaseController(messagesApi, env) {

  private val form = Form[ArticleTemplate](
    mapping(
      "id" -> optional(number),
      "headline" -> nonEmptyText,
      "body" -> nonEmptyText
    )(ArticleTemplate.apply)(ArticleTemplate.unapply)
  )

  def list = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    newsRepository.findArticleTemplates.map(articleTemplates =>
      Ok(views.html.articleTemplate.list(articleTemplates))
    )
  }

  def create = SecuredAction(WithRole(Role.Editor)) { implicit request =>
    val theForm = form.fill(ArticleTemplate())
    Ok(views.html.articleTemplate.edit(theForm))
  }

  def edit(id: Int) = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    for {
      articleTemplateOption <- newsRepository.findArticleTemplateById(id)
    } yield {
      articleTemplateOption match {
        case Some(articleTemplate) => Ok(views.html.articleTemplate.edit(form.fill(articleTemplate)))
        case None => NotFound
      }
    }
  }

  def show(id: Int) = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    for {
      articleTemplateOption <- newsRepository.findArticleTemplateById(id)
    } yield {
      articleTemplateOption match {
        case Some(articleTemplate) => Ok(views.html.articleTemplate.show(articleTemplate))
        case None => NotFound
      }
    }
  }

  def save = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.articleTemplate.edit(formWithErrors))),
      articleTemplate => {
        newsRepository.saveArticleTemplate(articleTemplate).map(savedArticleTemplate =>
          Redirect(routes.ArticleTemplateController.show(savedArticleTemplate.id.get)).flashing("success" -> Messages("articleTemplate.saved"))
        )
      }
    )
  }
}
