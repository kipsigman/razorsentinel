package controllers

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.SecuredErrorHandler

import play.api.Environment
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.Mode
import play.api.mvc.Results._
import play.api.mvc.{ Result, RequestHeader }
import play.api.routing.Router
import play.api.{ OptionalSourceMapper, Configuration }

import models.auth.User

@Singleton
class ErrorHandler @Inject() (
  environment: Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router],
  val messagesApi: MessagesApi)
  extends DefaultHttpErrorHandler(environment, config, sourceMapper, router)
  with SecuredErrorHandler with I18nSupport {

  override def onNotAuthenticated(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    implicit val rh = request
    implicit val userOption: Option[User] = None
    
    Some(Future.successful(Forbidden(views.html.error.fourOhOne())))
  }
  
  override def onNotAuthorized(request: RequestHeader, messages: Messages) = {
    implicit val rh = request
    implicit val userOption: Option[User] = None
    
    Some(Future.successful(Forbidden(views.html.error.fourOhThree())))
  }
  
  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    implicit val rh = request
    implicit val userOption: Option[User] = None
    
    Future.successful(NotFound(environment.mode match {
      case Mode.Prod => views.html.error.fourOhFour()
      case _ => views.html.defaultpages.devNotFound(request.method, request.uri, Option(router.get))
    }))
  }
}