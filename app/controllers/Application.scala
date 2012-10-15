package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import jp.t2v.lab.play20.auth.Auth


object Application extends Controller with Auth with AuthConfigImpl {

  def index = authorizedAction(NormalUser) { user =>
    implicit request =>
      Ok(views.html.index("The 'News' application is ready."))
  }

}