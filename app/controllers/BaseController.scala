package controllers

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.i18n.MessagesApi

import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.AuthController

import services.AdService

abstract class BaseController (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator])
  (implicit ec: ExecutionContext, protected val adService: AdService)
  extends AuthController(messagesApi, env) with ErrorResults {

}