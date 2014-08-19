package controllers

import javax.inject.{Inject, Singleton}

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models._
import models.Permission._
import services.NewsService

@Singleton
class Application @Inject() (newsService: NewsService) extends BaseController {

  /**
   * Health check handler for Amazon load balancer
   * @return
   */
  def health = Action {
    Ok("Healthy!")
  }
  
  def index = Action {implicit request =>
      Ok(views.html.index())
  }
  
  def indexAdmin = StackAction(AuthorityKey -> Administrator) { implicit request =>
    // val user = loggedIn
    Ok(views.html.indexAdmin())
  }
  
  
  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.ArticleController.updateTag,
        routes.javascript.Assets.at
      )
    ).as("text/javascript")
  }

}