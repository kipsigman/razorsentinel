package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.typesafe.config.Config

import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Location
import kipsigman.domain.entity.Page
import kipsigman.domain.entity.PageFilter
import kipsigman.play.auth.entity.User
import kipsigman.play.service.ImageService

import play.api.i18n.MessagesApi
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader
import play.api.mvc.Result

import models.Article
import models.ArticleTemplate

import models.ModelRepository
import models.NewsCategoryOptions
import services.ContentAuthorizationService
import services.WeatherService

@Singleton
class WidgetController @Inject() (
  config: Config,
  messagesApi: MessagesApi,
  ws: WSClient,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  contentAuthorizationService: ContentAuthorizationService,
  imageService: ImageService,
  weatherService: WeatherService
  )(implicit ec: ExecutionContext)
  extends ArticleContentController(messagesApi, env, modelRepository, contentAuthorizationService, imageService) {
  
  def recentArticles(categoryOption: Option[Category], excludeIds: Seq[Int], maxResults: Int = 3) = UserAwareAction.async {implicit request =>
    val category = categoryOption.getOrElse(NewsCategoryOptions.TopStories)
    modelRepository.findPublishedArticlesByCategory(category, PageFilter(0, maxResults + 1)).flatMap(page => {
      val articles = page.items.filterNot(article => excludeIds.contains(article.id.get)).sortBy(_.id).reverse.take(maxResults)
      articlesWithImages(articles).map(items => Ok(views.html.theme.allegro.widget.articles(categoryOption, items)))
    })
  }

  def trendingArticles(excludeIds: Seq[Int], maxResults: Int = 3) = UserAwareAction.async {implicit request =>
    val category = NewsCategoryOptions.TopStories
    modelRepository.findPublishedArticlesByCategory(category, PageFilter(0, maxResults + 1)).flatMap(page => {
      val articles = page.items.filterNot(article => excludeIds.contains(article.id.get)).sortBy(_.id).reverse.take(maxResults)
      articlesWithImages(articles).map(items => Ok(views.html.theme.allegro.widget.articles(Option(category), items)))
    })
  }
  
  def weather = UserAwareAction.async {implicit request =>
    val ip = request.request.remoteAddress
    val location = Location("Denver", "CO", None, "United States", None, None)
    weatherService.getWeatherByLocation(location).map(weather => {
      val weatherImage = weatherService.codeToImage(weather.code)
      Ok(views.html.theme.allegro.widget.weather(false, Option(weather), Option(weatherImage)))
    })
  }
}