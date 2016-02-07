package controllers

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import jsmessages.JsMessagesFactory
import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.User
import models.NewsRepository
import play.api.i18n.MessagesApi
import play.api.mvc.Action
import play.api.routing.JavaScriptReverseRouter

@Singleton
class AppController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  jsMessagesFactory: JsMessagesFactory,
  newsRepository: NewsRepository
  )(implicit ec: ExecutionContext) extends BaseController(messagesApi, env) {
  
  def health = Action {
    Ok("Healthy")
  }
  
  def help = UserAwareAction {implicit request =>
    Ok(views.html.help())
  }
  
  def index = UserAwareAction {implicit request =>
    Ok(views.html.index())
  }
  
  def section(category: Category) = UserAwareAction.async {implicit request =>
    for {
      articles <- newsRepository.findPublishedArticlesByCategory(category)
      templates <- newsRepository.findPublishedArticleTemplatesByCategory(category)
    } yield {
      // Only show Templates that aren't already represented by Articles
      val customizedTemplateIds: Set[Int] = articles.map(_.articleTemplateId).toSet
      val notCustomizedTemplates = templates.filterNot(template => customizedTemplateIds.contains(template.id.get))
      Ok(views.html.section(category, articles, notCustomizedTemplates))
    }
  }
  
  def jsMessages = Action {implicit request =>
    val messages = jsMessagesFactory.all
    Ok(messages(Some("window.Messages")))
  }

  def javascriptRoutes = Action {implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.ArticleController.saveStatus,
        routes.javascript.ArticleController.saveTag,
        routes.javascript.Assets.at
      )
    ).as("text/javascript")
  }

}