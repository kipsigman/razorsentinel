package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.exceptions.NotAuthorizedException
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import javax.inject.Singleton
import kipsigman.domain.entity.Content
import kipsigman.domain.entity.ContentImage
import kipsigman.domain.entity.Image
import kipsigman.play.auth.entity.User
import kipsigman.play.mvc.BodyParsers
import kipsigman.play.mvc.Html5ImageUpload
import kipsigman.play.service.ImageService
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.RequestHeader
import play.api.mvc.Result

import models.ModelRepository
import services.ContentAuthorizationService

abstract class ContentController[T <: Content[T]] (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  contentAuthorizationService: ContentAuthorizationService,
  imageService: ImageService)
  (implicit ec: ExecutionContext) 
  extends BaseController(messagesApi, env) {
  
  protected def findContent(id: Int): Future[Option[T]]
  
  protected def findContentWithImages(id: Int): Future[Option[(T, Seq[ContentImage])]] = {
    findContent(id) flatMap {
      case Some(content) => {
        imageService.findContentImages(content.contentClass, id).map(contentImages =>
          Option(content -> contentImages)
        )
      }
      case None => Future.successful(None)
    }
  }
  
  protected def canEdit(content: T)(implicit userOption: Option[User]): Boolean =
    contentAuthorizationService.canEdit[T](content)
  
  protected def authorizeEdit(id: Int)
    (implicit ec: ExecutionContext, userOption: Option[User]): Future[Option[T]] = {
    
    findContent(id).map(contentOption => contentOption.map(content => {
      if(canEdit(content)) {
        content
      } else {
        throw new NotAuthorizedException(s"User ${userOption} is not authorized to edit ${content.toString}")
      }  
    }))
  }
  
  protected def authorizeEditWithImages(id: Int)
    (implicit ec: ExecutionContext, userOption: Option[User]): Future[Option[(T, Seq[ContentImage])]] = {
    
    findContentWithImages(id).map(contentWithImagesOption => contentWithImagesOption.map(contentWithImages => {
      if(canEdit(contentWithImages._1)) {
        contentWithImages
      } else {
        throw new NotAuthorizedException(s"User ${userOption} is not authorized to edit ${contentWithImages._1.toString}")
      }  
    }))
  }
  
  protected def deleteContentImageCommon(contentId: Int, imageId: Int)(implicit request: RequestHeader, user: Option[User]): Future[Result] = {
    findContent(contentId) flatMap {
      case Some(content) => {
        imageService.deleteContentImage(content, imageId).map(success => {
          val responseJson = Json.obj("status"->"success", "id"-> imageId.toString)
          Ok(responseJson)  
        })    
      }
      case None => Future.successful(notFound)
    }
    
  }
  
  protected def saveContentImageCommon(contentId: Int, image: Image, imageUpload: Html5ImageUpload)(implicit request: RequestHeader, user: Option[User]): Future[Result] = {
    findContent(contentId).flatMap(contentOption => {
      imageService.saveContentImage(contentOption.get, image, imageUpload).map(contentImage => {
        val filename = contentImage.image.filename.get
        val url = kipsigman.play.mvc.routes.S3Controller.image(filename).url
        val responseJson = Json.obj("status" -> "success", "id" -> contentImage.image.id.get.toString, "url" -> url, "filename" -> filename)
        Ok(responseJson)
      })
    })
  }
  
  def deleteContentImage(contentId: Int, imageId: Int) = SecuredAction.async(parse.urlFormEncoded) {implicit request =>
    deleteContentImageCommon(contentId, imageId)
  }
  
  def saveContentImage(contentId: Int, imageId: Int) = SecuredAction.async(BodyParsers.html5ImageUpload) {implicit request =>
    val imageUpload = request.body
    val image = Image(id = Some(imageId), mimeType = imageUpload.mimeType, width = imageUpload.width, height = imageUpload.height)
    saveContentImageCommon(contentId, image, imageUpload)
  }
  
  def saveNewContentImage(contentId: Int) = SecuredAction.async(BodyParsers.html5ImageUpload) {implicit request =>
    val imageUpload = request.body
    val image = Image(mimeType = imageUpload.mimeType, width = imageUpload.width, height = imageUpload.height)
    saveContentImageCommon(contentId, image, imageUpload)
  }
}