package controllers

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.typesafe.config.Config
import kipsigman.play.auth.UserService
import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.SignInForm
import kipsigman.play.auth.mvc.SignUpForm
import kipsigman.play.auth.mvc.UserController
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.twirl.api.Html

@Singleton
class NewsUserController @Inject() (
  config: Config,
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher,
  clock: Clock)(implicit ec: ExecutionContext) 
  extends UserController(
    config,
    messagesApi,
    env,
    userService,
    authInfoRepository,
    credentialsProvider,
    avatarService,
    passwordHasher,
    clock: Clock) with NewsErrorResults {
  
  override protected def homeRoute: Call = routes.AppController.index
  
  override protected def signInRoute: Call = routes.NewsUserController.signIn
  
  override protected def signInView(form: Form[SignInForm.Data])(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.auth.signIn(form)
  
  override protected def signUpRoute: Call = routes.NewsUserController.signUp
  
  override protected def signUpView(form: Form[SignUpForm.Data])(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.auth.signUp(form)
  
  override protected def userView(implicit request: RequestHeader, user: User): Html =
    views.html.auth.user()
  
}