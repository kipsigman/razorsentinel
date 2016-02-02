package models

import play.api.db.slick._
import play.api.mvc.QueryStringBindable

import models.auth.User

trait IdEntity {
  def id: Option[Int]
  def isPersisted: Boolean = id.isDefined
  
  def isId(otherId: Int): Boolean = id.map(_ == otherId).getOrElse(false)
}

trait UserOwnedEntity extends IdEntity {
  def userId: Option[Int]
  
  def isOwnedBy(userOption: Option[User]): Boolean = (userId, userOption) match {
    case (Some(definedUserId), Some(user)) => user.isId(definedUserId)
    case _ => false
  }
  
  def isOwnedBy(user: User): Boolean = isOwnedBy(Option(user))
  
  def canEdit(userOption: Option[User]): Boolean = {
    (userId, userOption) match {
      // Entity has no owner, any User can edit
      case (None, _) => true
      // User has owner, only owner can edit
      case (Some(definedId), _) => isOwnedBy(userOption)
    }
  }
  
  def canEdit(user: User): Boolean = canEdit(Option(user))
}

trait ContentEntity extends UserOwnedEntity {
  import ContentEntity._
  
  def status: Status
  
  def isDeleted: Boolean = status == Status.Deleted
  def isDraft: Boolean = status == Status.Draft
  def isPublic: Boolean = status == Status.Public
  def isUnlisted: Boolean = status == Status.Unlisted
  def isPublished: Boolean = Status.publishValues.contains(status)
  
  def canPublish: Boolean
  
  def canView(userOption: Option[User]): Boolean = {
    if (isPublished) {
      true
    } else {
      isOwnedBy(userOption)
    }
  }
  
  def canView(user: User): Boolean = canView(Option(user))
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

trait CategorizedEntity {
  def categories: Set[Category]
  
  def categoriesSorted: Seq[Category] = categories.toSeq.sortBy(c => (c.order, c.name))
  
  def hasCategory(category: Category) = categories.contains(category)
}