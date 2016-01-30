package models.auth

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

import models.IdEntity

case class User(
  id: Option[Int],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  email: String,
  avatarURL: Option[String],
  roles: Set[Role] = Set(Role.Member)) extends IdEntity with Identity {
  
  lazy val name: String = firstName.getOrElse("") + lastName.map(ln => s" $ln").getOrElse("")
  
  /**
   * Checks for Role. Administrators are assumed to have all roles.
   */
  def hasRole(role: Role): Boolean = roles.contains(role) || roles.contains(Role.Administrator)
}

sealed abstract class Role(val name: String) {
  override def toString: String = name
}

object Role {
  // Site admin
  case object Administrator extends Role("administrator")
  // Can edit ArticleTemplates
  case object Editor extends Role("editor")
  // Can customize and manage Articles
  case object Member extends Role("member")
  
  val all: Set[Role] = Set(Administrator, Editor, Member)
  
  def apply(name: String): Role = {
    all.find(s => s.name == name) match {
      case Some(role) => role
      case None => throw new IllegalArgumentException(s"$name is not a valid Role")
    }
  }
}