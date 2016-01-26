package controllers

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import play.api.i18n.MessagesApi
import play.api.mvc.Action
import play.api.routing.JavaScriptReverseRouter

import models.NewsRepository
import models.User

@Singleton
class Application @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator])(implicit ec: ExecutionContext) extends BaseController(messagesApi, env) {

  /**
   * Health check handler for Amazon load balancer
   * @return
   */
  def health = Action {
    Ok("Healthy!")
  }
  
  def index = UserAwareAction { implicit request =>
    Ok(views.html.index())
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.ArticleController.updateTag,
        routes.javascript.Assets.at
      )
    ).as("text/javascript")
  }

}