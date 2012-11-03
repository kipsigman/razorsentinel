package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._


object Application extends Controller {

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

}