package controllers

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.DefaultAuthErrorHandler
import play.api.i18n.MessagesApi

import services.AdService

@Singleton
class ErrorHandler @Inject() (messagesApi: MessagesApi, env: Environment[User, CookieAuthenticator])(implicit protected val adService: AdService)
  extends DefaultAuthErrorHandler(messagesApi, env) with ErrorResults