package controllers

import java.sql.Timestamp

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.typesafe.config.Config

import play.api.i18n.{Messages, MessagesApi, I18nSupport}
import play.api.mvc._

import models.NewsRepository
import models.auth.Role
import models.auth.User

/**
 * Base controller for app. Includes Auth behavior, etc.
 */
abstract class BaseController(
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator]) extends Silhouette[User, CookieAuthenticator] {
  
  protected object FlashKey {
    val error = "error"
    val info = "info"
    val success = "success"
    val warning = "warning"
  }
  
  protected implicit def request2UserOption(implicit request: UserAwareRequest[play.api.mvc.AnyContent]): Option[User] = request.identity
  protected implicit def request2User(implicit request: SecuredRequest[play.api.mvc.AnyContent]): User = request.identity
  protected implicit def user2UserOption(implicit user: User): Option[User] = Option(user)
  
  protected def forbidden(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Result = Forbidden(views.html.error.fourOhThree())
  
  protected def notFound(implicit request: RequestHeader, user: Option[User]): Result = NotFound(views.html.error.fourOhFour())
}

/**
 * Only allows those user that have at least a role of the selected.
 * Administrator role is always allowed.
 * Ex: WithRole(Editor, Member)
 * @see http://silhouette.mohiva.com/docs/authorization
 */
case class WithRole(anyOf: Role*) extends Authorization[User, CookieAuthenticator] {
  def isAuthorized[A](user: User, authenticator: CookieAuthenticator)(implicit r: Request[A], m: Messages) = Future.successful {
    WithRole.isAuthorized(user, anyOf: _*)
  }
}
object WithRole {
  def isAuthorized(user: User, anyOf: Role*): Boolean =
    anyOf.intersect(user.roles.toSeq).size > 0 || user.roles.contains(Role.Administrator)
}