package models.auth.slick

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import play.api.db.slick.DatabaseConfigProvider
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import models.auth.User
import models.auth.UserRepository

@Singleton()
class UserRepositorySlick @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends UserRepository with AuthDBConfig {
  import driver.api._
  
  override protected val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  
  // queries used in multiple places
  private def findLoginInfo(loginInfo: LoginInfo) = 
    dbLoginInfoQuery.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def find(loginInfo: LoginInfo) = {
    val userQuery = for {
      dbLoginInfo <- findLoginInfo(loginInfo)
      dbUserLoginInfo <- dbUserLoginInfoQuery.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- dbUserQuery.filter(_.id === dbUserLoginInfo.userId)
    } yield dbUser
    db.run(dbUserQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.id, loginInfo, user.firstName, user.lastName, user.email, user.avatarURL, user.roles)
      }
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  override def find(id: Int) = {
    val query = for {
      dbUser <- dbUserQuery.filter(_.id === id)
      dbUserLoginInfo <- dbUserLoginInfoQuery.filter(_.userId === dbUser.id)
      dbLoginInfo <- dbLoginInfoQuery.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(
            user.id,
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.firstName,
            user.lastName,
            user.email,
            user.avatarURL,
            user.roles)
      }
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User) = {
    val dbUser = DBUser(user.id, user.firstName, user.lastName, user.email, user.avatarURL, user.roles)
    val dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.    
    val loginInfoAction = {
      val retrieveLoginInfo = dbLoginInfoQuery.filter(
        info => info.providerID === user.loginInfo.providerID &&
        info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = dbLoginInfoQuery.returning(dbLoginInfoQuery.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    
    // combine database actions to be run sequentially
    val actions = (for {
      userId <- dbUserQuery.returning(dbUserQuery.map(_.id)).insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- dbUserLoginInfoQuery += DBUserLoginInfo(userId.get, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
