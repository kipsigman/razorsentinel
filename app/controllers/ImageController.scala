package controllers

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.play.service.ImageService
import kipsigman.play.auth.entity.User
import play.api.i18n.MessagesApi

import models.ModelRepository

@Singleton
class ImageController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  imageService: ImageService)
  (implicit ec: ExecutionContext)
  extends BaseController(messagesApi, env) {
  
  def imageDropzone(id: Int) = SecuredAction.async {implicit request =>
    imageService.findImage(id) map {
      case Some(image) => Ok(views.html.content.imageDropzone(Option(image)))
      case None => notFound
    }
  }
  
}