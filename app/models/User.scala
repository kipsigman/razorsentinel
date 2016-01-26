package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

case class User(
  id: Option[Int],
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  email: Option[String],
  avatarURL: Option[String]) extends IdEntity with Identity {
  
  lazy val name: String = firstName.getOrElse("") + lastName.map(ln => s" $ln").getOrElse("")
}