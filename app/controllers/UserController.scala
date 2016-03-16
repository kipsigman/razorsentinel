package controllers

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.typesafe.config.Config
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.play.auth.UserService
import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.SignInForm
import kipsigman.play.auth.mvc.SignUpForm
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import services.AdService

@Singleton
class UserController @Inject() (
  config: Config,
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher,
  clock: Clock)(implicit ec: ExecutionContext, protected val adService: AdService) 
  extends kipsigman.play.auth.mvc.UserController(
    config,
    messagesApi,
    env,
    userService,
    authInfoRepository,
    credentialsProvider,
    avatarService,
    passwordHasher,
    clock: Clock) with ErrorResults {
  
  override protected def homeRoute: Call = routes.SiteController.index
  
  override protected def signInRoute: Call = routes.UserController.signIn(None)
  
  override protected def signInView(form: Form[SignInForm.Data])(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.auth.signIn(form)
  
  override protected def signUpRoute: Call = routes.UserController.signUp(None)
  
  override protected def signUpView(form: Form[SignUpForm.Data])(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.auth.signUp(form)
  
  override protected def userView(implicit request: RequestHeader, user: User): Html =
    views.html.auth.user()
}