package models.slick

import models._

trait SilhoutteTableDefinitions extends TableDefinitions {

  import driver.api._
  
  case class DBUser (
    id: Option[Int],
    firstName: Option[String],
    lastName: Option[String],
    email: Option[String],
    avatarURL: Option[String]
  ) extends IdEntity
  
  class DBUserTable(tag: Tag) extends IdTable[DBUser](tag, "user") {
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatar_url")
    def * = (id.?, firstName, lastName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(
    id: Option[Int],
    providerID: String,
    providerKey: String
  ) extends IdEntity

  class DBLoginInfoTable(tag: Tag) extends IdTable[DBLoginInfo](tag, "login_info") {
    def providerID = column[String]("provider_id")
    def providerKey = column[String]("provider_key")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
    userId: Int,
    loginInfoId: Int
  )

  class DBUserLoginInfoTable(tag: Tag) extends Table[DBUserLoginInfo](tag, "user_login_info") {
    def userId = column[Int]("user_id")
    def loginInfoId = column[Int]("login_info_id")
    def * = (userId, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
    loginInfoId: Int,
    hasher: String,
    password: String,
    salt: Option[String]
  )

  class DBPasswordInfoTable(tag: Tag) extends Table[DBPasswordInfo](tag, "password_info") {
    def loginInfoId = column[Int]("login_info_id")
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def * = (loginInfoId, hasher, password, salt) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }
  
  val dbUserQuery = TableQuery[DBUserTable]
  val dbLoginInfoQuery = TableQuery[DBLoginInfoTable]
  val dbUserLoginInfoQuery = TableQuery[DBUserLoginInfoTable]
  val dbPasswordInfoQuery = TableQuery[DBPasswordInfoTable]
}