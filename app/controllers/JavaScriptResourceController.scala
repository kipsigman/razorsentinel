package controllers

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.typesafe.config.Config

import javax.inject.Inject
import javax.inject.Singleton
import jsmessages.JsMessagesFactory
import kipsigman.play.auth.entity.User
import play.api.i18n.MessagesApi
import play.api.routing.JavaScriptReverseRoute

@Singleton
class JavaScriptResourceController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  protected val config: Config,
  protected val jsMessagesFactory: JsMessagesFactory
  )(implicit ec: ExecutionContext) extends BaseController(messagesApi, env) with kipsigman.play.mvc.JavaScriptResourceController {
  
  override protected def javaScriptReverseRoutes: Seq[JavaScriptReverseRoute] = Seq(
    controllers.routes.javascript.Assets.at,
    controllers.routes.javascript.WidgetController.weather,
    controllers.routes.javascript.ArticleController.create,
    controllers.routes.javascript.ArticleController.createPost,
    controllers.routes.javascript.ArticleController.edit,
    controllers.routes.javascript.ArticleController.editPost,
    controllers.routes.javascript.ArticleController.saveStatus,
    controllers.routes.javascript.ArticleController.saveTag,
    controllers.routes.javascript.ImageController.imageDropzone,
    controllers.routes.javascript.ArticleTemplateController.deleteContentImage,
    controllers.routes.javascript.ArticleTemplateController.saveContentImage,
    controllers.routes.javascript.ArticleTemplateController.saveNewContentImage    
  )
}