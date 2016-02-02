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
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.RequestHeader

import models._
import models.auth.Role
import models.auth.UnauthorizedOperationException
import models.auth.User
import services.UrlService

@Singleton
class ArticleController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  newsRepository: NewsRepository,
  urlService: UrlService)(implicit ec: ExecutionContext) extends BaseController(messagesApi, env) {

  //  
//  private val statusOptions: Seq[(String, String)] =
//    ContentEntity.Status.activeValues.toSeq.sortBy(_.name).map(status => status.name -> Messages(s"article.status.${status}"))
  
  private case class TagData(
    articleId: Int,
    name: String,
    value: String)

  private val tagForm = Form[TagData](
    mapping(
      "pk" -> number,
      "name" -> nonEmptyText,
      "value" -> nonEmptyText
    )(TagData.apply)(TagData.unapply)
  )
  
  def create(articleTemplateId: Int) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleTemplateById(articleTemplateId) flatMap {
      case Some(articleTemplate) => {
        val article = Article(userId = request.identity.flatMap(_.id), articleTemplateId = articleTemplate.id.get)
        newsRepository.saveArticle(article).map(savedArticle => {
          val articleInflated = ArticleInflated(savedArticle, articleTemplate)
          Ok(views.html.article.edit(articleInflated))
        })
      }
      case None => Future.successful(notFound)
    }
  }
  
  def delete(id: Int) = SecuredAction(WithRole(Role.Member)).async { implicit request =>
    newsRepository.deleteArticle(id) map {
      case Some(article) => Redirect(routes.ArticleController.list()).flashing(FlashKey.success -> Messages("action.delete.success")) 
      case None => notFound
    } recover {
      case t: UnauthorizedOperationException => Forbidden(views.html.error.fourOhThree(Option(Messages("article.error.unauthorized")))) 
    }
  }
  
  def edit(id: Int) = SecuredAction(WithRole(Role.Member)).async { implicit request =>
    newsRepository.findArticleInflatedById(id) map {
      case Some(article) => {
        if(article.isOwnedBy(request.identity)) {
          Ok(views.html.article.edit(article))
        } else {
          Forbidden(views.html.error.fourOhThree(Option(Messages("article.error.unauthorized"))))
        }
      }
      case None => notFound
    }
  }
  
  def list = SecuredAction(WithRole(Role.Member)).async { implicit request =>
    newsRepository.findArticlesByUser(request.identity).map(articles =>
      Ok(views.html.article.list(articles))
    )
  }
  
  def preview(id: Int) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleInflatedById(id) map {
      case Some(article) => {
        if(article.isPublished) {
          // If published redirect to view to display proper URL
          Redirect(routes.ArticleController.view(article.seoAlias))
        } else {
          Ok(views.html.article.view(article))
        }
      }
      case None => notFound
    }
  }
  
  /**
   * seoAlias can be just the id or the id with headline.
   */
  def view(seoAlias: String) = UserAwareAction.async { implicit request =>
    // /article/99-the-article-headline-url-friendly
    val firstDash = seoAlias.indexOf('-')
    try {
      val id: Int = firstDash match {
        case -1 => seoAlias.toInt
        case x if(x > 0) => seoAlias.substring(0, firstDash).toInt
        case x => throw new IllegalArgumentException(s"$seoAlias is an invalid article path")
      }
      newsRepository.findArticleInflatedById(id) map {
        case Some(article) => Ok(views.html.article.view(article))
        case None => notFound
      }  
    } catch {
      case t : Throwable => Future.successful(notFound)
    }
  }
  
  private def saveSuccessJson(article: ArticleInflated): JsValue = {
    Json.toJson(Map("id" -> JsString(article.id.get.toString), "status" -> JsString(article.status.name), "canPublish" -> JsBoolean(article.canPublish)))
  }

  def saveStatus(id: Int, status: ContentEntity.Status) = UserAwareAction.async { implicit request =>
    val userOption = request.identity
    if(status == ContentEntity.Status.Public && (userOption.isEmpty || !userOption.get.isEditor)) {
      // Only editors may publish public
      Future.successful(forbidden())
    } else {
      for {
        article <- newsRepository.updateArticleStatus(id, status)
      } yield {
        Redirect(routes.ArticleController.edit(id)).flashing(FlashKey.success -> Messages("content.status.save.success"))
      }
    }
  }
  
  def saveTag(id: Int) = UserAwareAction.async { implicit request =>
    tagForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("Bad data")),
      data => {
        logger.debug(s"data=$data")
        for {
          article <- newsRepository.addTagReplacement(id, TagReplacement(data.name, data.value))
        } yield {
          Ok(saveSuccessJson(article))
        }
      }
    )
  }
}