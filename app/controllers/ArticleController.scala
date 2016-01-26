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
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.RequestHeader

import models._
import services.UrlService

@Singleton
class ArticleController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  newsRepository: NewsRepository,
  urlService: UrlService)(implicit ec: ExecutionContext) extends BaseController(messagesApi, env) {

  private case class UpdateTagData(
    articleId: Int,
    articleTemplateId: Int,
    name: String,
    value: String)

  private val updateTagForm = Form[UpdateTagData](
    mapping(
      "pk" -> number,
      "articleTemplateId" -> number,
      "name" -> nonEmptyText,
      "value" -> nonEmptyText
    )(UpdateTagData.apply)(UpdateTagData.unapply)
  )

  def updateTag = UserAwareAction.async { implicit request =>
    updateTagForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("Bad data")),
      data => {
        logger.debug(s"data=$data")
        for {
          articleInflated <- newsRepository.addTagReplacement(data.articleId, TagReplacement(data.name, data.value))
          url <- preparedUrl(request, articleInflated)
        } yield {
          if (articleInflated.article.publish) {
            // All tags replaced, give URL for sharing
            val json = Json.toJson(Map("status" -> "PUBLISH", "url" -> url, "id" -> articleInflated.article.id.get.toString))
            Ok(json)
          } else {
            // Not completely customized
            val json = Json.toJson(Map("status" -> "DRAFT", "url" -> url, "articleId" -> articleInflated.article.id.get.toString))
            Ok(json)
          }
        }
      }
    )
  }

  def listArticleTemplates = UserAwareAction.async { implicit request =>
    newsRepository.findArticleTemplates.map(articleTemplates =>
      Ok(views.html.article.listArticleTemplates(articleTemplates))
    )
  }

  def create(articleTemplateId: Int) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleTemplateById(articleTemplateId) flatMap {
      case Some(articleTemplate) => {
        val article = Article(None, articleTemplate.id.get, None, false)
        newsRepository.saveArticle(article).map(savedArticle => {
          Ok(views.html.article.create(savedArticle, articleTemplate))
        })
      }
      case None => Future.successful(notFound)
    }
  }

  def show(seoAlias: String) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleBySeoAlias(seoAlias) flatMap {
      case Some(article) => {
        newsRepository.findArticleTemplateById(article.articleTemplateId) map {
          case Some(at) => Ok(views.html.article.show(ArticleInflated(article, at)))
          case None => notFound
        }

      }
      case None => Future.successful(notFound)
    }
  }

  private def preparedUrl(request: RequestHeader, articleInflated: ArticleInflated): Future[String] = {
    val absoluteUrl = urlService.absoluteUrl(request, articleInflated.relativeUrl)
    //urlService.shortenUrl(absoluteUrl)
    Future.successful(absoluteUrl)
  }

}