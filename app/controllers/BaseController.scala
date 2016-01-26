package controllers

import java.sql.Timestamp

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.typesafe.config.Config

import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.mvc._

import models.NewsRepository
import models.User

/**
 * Base controller for app. Includes Auth behavior, etc.
 */
abstract class BaseController(
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator]) extends Silhouette[User, CookieAuthenticator] {
  
  protected implicit def request2UserOption(implicit request: UserAwareRequest[play.api.mvc.AnyContent]): Option[User] = request.identity
  protected implicit def request2User(implicit request: SecuredRequest[play.api.mvc.AnyContent]): User = request.identity
  
  protected def notFound(implicit request: RequestHeader, user: Option[User]): Result = NotFound(views.html.notFound())
}