package controllers

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.SecuredErrorHandler
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.Configuration
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.Results._
import play.api.mvc.{ Result, RequestHeader }
import play.api.OptionalSourceMapper
import play.api.routing.Router
import play.twirl.api.Html

import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.DefaultAuthErrorHandler

@Singleton
class NewsErrorHandler @Inject() (
  environment: play.api.Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router],
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator])
  extends DefaultAuthErrorHandler(environment, config, sourceMapper, router, messagesApi, env) with NewsErrorResults {
  
}