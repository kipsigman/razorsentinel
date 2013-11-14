package controllers

import scala.concurrent.{ExecutionContext, Future}
import jp.t2v.lab.play2.auth.{AuthConfig,CookieIdContainer,IdContainer,LoginLogout}
import models.Permission._
import models.User
import play.api.data.Form
import play.api.data.Forms.email
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.mvc.SimpleResult
import play.api.mvc.RequestHeader
import play.api.mvc.Results.{Forbidden,Redirect,Unauthorized}
import reflect.{ClassTag, classTag}


/**
 * Flows for login/logout and security definitions.
 * @author kip
 */
object AuthController extends Controller with LoginLogout with AuthConfigImpl {
  
  /** Your application's login form.  Alter it to fit your application */
  val loginForm = Form {
    mapping("email" -> email, "password" -> nonEmptyText)(User.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }
  
  
/**
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `Future[SimpleResult]`,
   * you can add a procedure like the following.
   *
   *   gotoLogoutSucceeded.map(_.flashing(
   *     "success" -> "You've been logged out"
   *   ))
   */
  def logout = Action.async { implicit request =>
    // do something...
    gotoLogoutSucceeded
  }
  

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   *
   * Since the `gotoLoginSucceeded` returns `Future[SimpleResult]`,
   * you can add a procedure like the `gotoLogoutSucceeded`.
   */
  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      user => gotoLoginSucceeded(user.get.id)
    )
  }
  
}

trait AuthConfigImpl extends AuthConfig {

  /**
   * A type that is used to identify a user.
   * `String`, `Int`, `Long` and so on.
   */
  type Id = Long

  /**
   * A type that represents a user in your application.
   * `User`, `Account` and so on.
   */
  type User = models.User

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Permission
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */
  type Authority = Permission

  /**
   * A `ClassManifest` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  val idTag: ClassTag[Id] = classTag[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext): Future[Option[User]] = Future.successful(models.User.findById(id))

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] =
    Future.successful({
      val uri = request.session.get("access_uri").getOrElse(routes.Application.index.url.toString)
      request.session - "access_uri"
      Redirect(uri)
    })

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] =
    Future.successful(Redirect(routes.AuthController.login))

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] = {
    Future.successful{
      // Check for request type. If AJAX request return an Unauthorized.
      // Otherwise redirect to login page.
      request.headers.get("X-Requested-With") match {
        case Some("XMLHttpRequest") => Unauthorized("Unauthorized. User session may have expired.")
        case _ => Redirect(routes.AuthController.login).withSession("access_uri" -> request.uri) 
      }
    }
  }

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[SimpleResult] = 
    Future.successful(Forbidden("no permission"))

  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.permission, authority) match {
      case (Administrator, _)       => true
      case _                        => false
    }
  }
  
  /**
   * Whether use the secure option or not use it in the cookie.
   * However default is false, I strongly recommend using true in a production.
   */
  override lazy val cookieSecureOption: Boolean = play.api.Play.current.configuration.getBoolean("auth.cookie.secure").getOrElse(true)
  
  /**
   * Overriding for "Stateless" implementation. See https://github.com/t2v/play20-auth for more info.
   * @param request
   * @return
   */
  override lazy val idContainer: IdContainer[Id] = new CookieIdContainer[Id]
}
