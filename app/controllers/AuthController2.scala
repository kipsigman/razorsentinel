package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.data._
import play.api.data.Forms._
import jp.t2v.lab.play20.auth._
import models._

object AuthController2 extends Controller with LoginLogout with AuthConfigImpl {
  
    /** Your application's login form.  Alter it to fit your application */
  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(Account.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  def login = Action { implicit request =>
    Ok(views.html.login2(loginForm))
  }

  /** 
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `PlainResult`, 
   * you can add a procedure like the following.
   * 
   *   gotoLogoutSucceeded.flashing(
   *     "success" -> "You've been logged out"
   *   )
   */
  def logout = Action { implicit request =>
    // do something...
    gotoLogoutSucceeded
  }

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   * 
   * Since the `gotoLoginSucceeded` returns `PlainResult`, 
   * you can add a procedure like the `gotoLogoutSucceeded`.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login2(formWithErrors)),
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
  type User = Account

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
  val idManifest: ClassManifest[Id] = classManifest[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id): Option[User] = Account.findById(id)

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded[A](request: Request[A]): PlainResult = {
    val uri = request.session.get("access_uri").getOrElse("/")
    request.session - "access_uri"
    Redirect(uri)
  }

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded[A](request: Request[A]): PlainResult = Redirect(routes.AuthController2.login)
  
  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed[A](request: Request[A]): PlainResult = 
    Redirect(routes.AuthController2.login).withSession("access_uri" -> request.uri)
  

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed[A](request: Request[A]): PlainResult = Forbidden("no permission")
  
  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority): Boolean = 
    (user.permission, authority) match {
      case (Administrator, _) => true
      case (NormalUser, NormalUser) => true
      case _ => false
    }

}