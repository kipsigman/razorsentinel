package models

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.exceptions.NotAuthorizedException
import kipsigman.play.auth.entity.User
import kipsigman.play.auth.entity.UserOwnedEntity
import play.api.mvc.QueryStringBindable

trait ContentEntity[T <: ContentEntity[T]] extends UserOwnedEntity {
  import ContentEntity._
  
  def status: Status
  
  def canPublish: Boolean
  
  protected def updateStatusCopy(newStatus: Status): T
  
  final def isDeleted: Boolean = status == Status.Deleted
  final def isDraft: Boolean = status == Status.Draft
  final def isPublic: Boolean = status == Status.Public
  final def isUnlisted: Boolean = status == Status.Unlisted
  final def isPublished: Boolean = Status.publishValues.contains(status)
  
  final def canView(userOption: Option[User]): Boolean = {
    if (isPublished) {
      true
    } else {
      isOwnedBy(userOption)
    }
  }
  
  final def canView(user: User): Boolean = canView(Option(user))
  
  final def updateStatus(newStatus: Status)(implicit user: Option[User]): T = {
    val statusChangeOk = (status, newStatus) match {
      case (Status.Deleted, _) => false 
      case (_, Status.Public) => user.map(_.isEditor).getOrElse(false)
      case (_, _) => true
    }
    
    if (statusChangeOk) {
      updateStatusCopy(newStatus)   
    } else {
      throw new IllegalStatusChangeException(status, newStatus)
    }
  }
  
  final def delete(implicit user: Option[User]): T = this.updateStatus(Status.Deleted)
  
  final def publishPublic(implicit user: Option[User]): T = this.updateStatus(Status.Public)
  
  final def publishUnlisted(implicit user: Option[User]): T = this.updateStatus(Status.Unlisted)
  
  final def revertToDraft(implicit user: Option[User]): T = this.updateStatus(Status.Draft)
}

object ContentEntity {
  sealed abstract class Status(val name: String) {
    override def toString: String = name
  }

  object Status {
    case object Deleted extends Status("deleted")
    case object Draft extends Status("draft")
    case object Public extends Status("public")
    case object Unlisted extends Status("unlisted")
    
    val all: Set[Status] = Set(Deleted, Draft, Public, Unlisted)
    val activeValues: Set[Status] = Set(Draft, Public, Unlisted)
    val publishValues: Set[Status] = Set(Public, Unlisted)
    
    def apply(name: String): Status = {
      all.find(s => s.name == name) match {
        case Some(status) => status
        case None => throw new IllegalArgumentException(s"Invalid Status: $name")
      }
    }
    
    implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[Status] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Status]] = {
        for {
          nameEither <- stringBinder.bind(key, params)
        } yield {
          nameEither match {
            case Right(name) => Right(Status(name))
            case _ => Left("Unable to bind Status")
          }
        }
      }
      override def unbind(key: String, value: Status): String = stringBinder.unbind(key, value.name)
    }
  }
}

class IllegalStatusChangeException(oldStatus: ContentEntity.Status, newStatus: ContentEntity.Status) 
  extends RuntimeException(s"Illegal status change: ${oldStatus.name} -> ${newStatus.name}")

trait ContentRepository {
  
  implicit protected def ec: ExecutionContext
  
  protected def authorizedEdit[T <: ContentEntity[T]](entity: T, userOption: Option[User])(editOperation: T => Future[T]) = {
    if(entity.canEdit(userOption)) {
      editOperation(entity)
    } else {
      throw new NotAuthorizedException(s"User is not authorized to edit entity: ${entity.toString}")
    }
  }
  
  protected def authorizedFindForEdit[T <: ContentEntity[T]](id: Int, userOption: Option[User])(findOperation: Int => Future[Option[T]]) = {
    findOperation(id).map(entityOption => entityOption.map(entity => {
      if(entity.canEdit(userOption)) {
        entity
      } else {
        throw new NotAuthorizedException(s"User is not authorized to edit entity: ${entity.toString}")
      }  
    }))
  }
}
