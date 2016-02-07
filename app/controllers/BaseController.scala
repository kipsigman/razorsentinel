package controllers

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.i18n.MessagesApi

import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.AuthController

abstract class BaseController (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator])
  (implicit ec: ExecutionContext)
  extends AuthController(messagesApi, env) with NewsErrorResults {
  
  // Assumes Anonymous User has been created in the DB
  protected lazy val anonymousUserId: Int = 1
}