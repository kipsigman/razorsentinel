package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.exceptions.NotAuthorizedException
import kipsigman.play.auth.entity.User

import models.Content
import services.ContentAuthorizationService

trait ContentAuthorizationController[T <: Content[T]] {
  protected def contentAuthorizationService: ContentAuthorizationService
  
  protected def canEdit(content: T)(implicit userOption: Option[User]): Boolean =
    contentAuthorizationService.canEdit[T](content)
    
  protected def findContent(id: Int): Future[Option[T]]
  
  protected def authorizeEdit(id: Int)
    (implicit ec: ExecutionContext, userOption: Option[User]): Future[Option[T]] = authorizeEditWithFind(id)(findContent)
  
  protected def authorizeEditWithFind(id: Int)
    (findOperation: Int => Future[Option[T]])
    (implicit ec: ExecutionContext, userOption: Option[User]): Future[Option[T]] = {
    
    findOperation(id).map(contentOption => contentOption.map(content => {
      if(canEdit(content)) {
        content
      } else {
        throw new NotAuthorizedException(s"User ${userOption} is not authorized to edit ${content.toString}")
      }  
    }))
  }
}