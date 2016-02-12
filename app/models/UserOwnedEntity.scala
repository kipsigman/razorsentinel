package models

import kipsigman.domain.entity.IdEntity
import kipsigman.play.auth.entity.User

trait UserOwnedEntity extends IdEntity {
  def userIdOption: Option[Int]
  
  final def isOwnedBy(userOption: Option[User]): Boolean = (userIdOption, userOption) match {
    case (Some(definedUserId), Some(user)) => user.isId(definedUserId)
    case _ => false
  }
}