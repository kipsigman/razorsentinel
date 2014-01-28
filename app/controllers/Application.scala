package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models._
import models.Permission._


object Application extends BaseController {

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
        ArticleController.updateTag
      )
    ).as("text/javascript")
  }

}