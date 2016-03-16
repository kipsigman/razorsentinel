package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Content
import kipsigman.domain.entity.ContentImage
import kipsigman.play.auth.entity.User
import kipsigman.play.service.ImageService
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader

import models.Article
import models.ArticleTemplate
import models.ArticleWithImages
import models.ModelRepository
import services.AdService
import services.ContentAuthorizationService

abstract class ArticleContentController (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  contentAuthorizationService: ContentAuthorizationService,
  imageService: ImageService)
  (implicit ec: ExecutionContext, adService: AdService) 
  extends ContentController[Article](messagesApi, env, modelRepository, contentAuthorizationService, imageService) {
  
  override protected def findContent(id: Int): Future[Option[Article]] = modelRepository.findArticle(id)
  
  protected def articlesWithImages(articles: Seq[Article])
    (implicit request: RequestHeader, user: Option[User]): Future[Seq[ArticleWithImages]] = {
    
    val articleTemplateIds = articles.map(_.articleTemplate.id.get).toSet
    for {
      contentImages <- imageService.findContentImages(ArticleTemplate.contentClass, articleTemplateIds)
    } yield {
      articles.map(article => 
        ArticleWithImages(article, contentImages.filter(_.contentId == article.articleTemplate.id.get))
      )
    }
  }
  
  protected def templateIds(articles: Seq[Article]): Set[Int] = articles.map(_.articleTemplate.id.get).toSet
}