package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json

import models.Article
import models.ArticleInflated
import models.Content
import models.NewsRepository
import models.TagReplacement
import services.ContentAuthorizationService

@Singleton
class ArticleController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  newsRepository: NewsRepository,
  protected val contentAuthorizationService: ContentAuthorizationService)(implicit ec: ExecutionContext) 
  extends BaseController(messagesApi, env) with ContentAuthorizationController[ArticleInflated] {
  
  protected def findContent(id: Int): Future[Option[ArticleInflated]] = newsRepository.findArticleInflated(id)
  
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
    newsRepository.findArticleTemplate(articleTemplateId) flatMap {
      case Some(articleTemplate) => {
        val article = Article(userIdOption = request.identity.flatMap(_.id), articleTemplateId = articleTemplate.id.get)
        newsRepository.saveArticle(article).map(savedArticle => {
          val articleInflated = ArticleInflated(savedArticle, articleTemplate)
          Ok(views.html.article.edit(articleInflated))
        })
      }
      case None => Future.successful(notFound)
    }
  }
  
  def delete(id: Int) = saveStatus(id, Content.Status.Deleted)
  
  def edit(id: Int) = UserAwareAction.async { implicit request =>
    authorizeEdit(id) map {
      case Some(article) => Ok(views.html.article.edit(article))
      case None => notFound
    }
  }
  
  def list = SecuredAction(WithRole(Role.Member)).async { implicit request =>
    newsRepository.findArticlesByUser(request.identity).map(articles =>
      Ok(views.html.article.list(articles))
    )
  }
  
  def preview(id: Int) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleInflated(id) map {
      case Some(article) => {
        if(article.isPublished) {
          // If published redirect to view to display proper URL
          Redirect(routes.ArticleController.view(article.category, article.seoAlias, true))
        } else {
          Ok(views.html.article.view(article, true, contentAuthorizationService))
        }
      }
      case None => notFound
    }
  }
  
  /**
   * seoAlias can be just the id or the id with headline.
   */
  def view(category: Category, seoAlias: String, preview: Boolean = false) = UserAwareAction.async { implicit request =>
    // /article/99-the-article-headline-url-friendly
    val firstDash = seoAlias.indexOf('-')
    try {
      val id: Int = firstDash match {
        case -1 => seoAlias.toInt
        case x if(x > 0) => seoAlias.substring(0, firstDash).toInt
        case x => throw new IllegalArgumentException(s"$seoAlias is an invalid article path")
      }
      newsRepository.findArticleInflated(id) map {
        case Some(article) => Ok(views.html.article.view(article, preview, contentAuthorizationService))
        case None => notFound
      }  
    } catch {
      case t : Throwable => Future.successful(notFound)
    }
  }
  
  private def saveSuccessJson(article: ArticleInflated): JsValue = {
    Json.toJson(Map("id" -> JsString(article.id.get.toString), "status" -> JsString(article.status.name), "canPublish" -> JsBoolean(article.canPublish)))
  }

  def saveStatus(id: Int, status: Content.Status) = UserAwareAction.async { implicit request =>
    authorizeEdit(id).flatMap(articleOption =>
      for {
        savedArticle <- newsRepository.updateArticleStatus(id, status)
      } yield {
        if (savedArticle.isPublished) {
          Redirect(routes.ArticleController.view(savedArticle.category, savedArticle.seoAlias, true)).flashing(FlashKey.success -> Messages("content.status.save.success"))
        } else {
          Redirect(routes.ArticleController.edit(id)).flashing(FlashKey.success -> Messages("content.status.save.success"))
        }
      }
    )
  }
  
  def saveTag(id: Int) = UserAwareAction.async { implicit request =>
    tagForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("Bad data")),
      data => {
        authorizeEdit(id).flatMap(articleOption =>
          for {
            savedArticle <- newsRepository.addTagReplacement(id, TagReplacement(data.name, data.value))
          } yield {
            Ok(saveSuccessJson(savedArticle))
          }
        )
      }
    )
  }
}