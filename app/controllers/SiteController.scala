package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import jsmessages.JsMessagesFactory
import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Page
import kipsigman.domain.entity.PageFilter
import kipsigman.play.auth.entity.User
import kipsigman.play.service.ImageService
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader
import play.api.mvc.Result

import models._
import services.ContentAuthorizationService

@Singleton
class SiteController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  contentAuthorizationService: ContentAuthorizationService,
  imageService: ImageService
  )(implicit ec: ExecutionContext)
  extends ArticleContentController(messagesApi, env, modelRepository, contentAuthorizationService, imageService) {
  
  def about = UserAwareAction {implicit request =>
    Ok(views.html.about())
  }
  
  def contact = UserAwareAction {implicit request =>
    Ok(views.html.contact())
  }
  
  def help = UserAwareAction {implicit request =>
    Ok(views.html.help())
  }
  
  def index = UserAwareAction.async {implicit request =>
    modelRepository.findPublishedArticlesByCategory(NewsCategoryOptions.TopStories, PageFilter(0, 10)).flatMap(page =>
      articlesWithImages(page.items).map(itemsWithImages => {
        val pageWithImages = Page(itemsWithImages, page.pageFilter, page.hasNext)
        Ok(views.html.index(pageWithImages.items))
      })
    )
  }
  
  def category(category: Category, pageIndex: Int = 0) = UserAwareAction.async {implicit request =>
    modelRepository.findPublishedArticlesByCategory(category, PageFilter(pageIndex, 10)).flatMap(page =>
      articlesWithImages(page.items).map(itemsWithImages => {
        val pageWithImages = Page(itemsWithImages, page.pageFilter, page.hasNext)
        Ok(views.html.category(category, pageWithImages))
      })
    )
  }
  
}