package models

import java.util.TimeZone

import org.mindrot.jbcrypt.BCrypt
import org.squeryl.PrimitiveTypeMode.__thisDsl
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.PrimitiveTypeMode.orderByArg2OrderByExpression
import org.squeryl.PrimitiveTypeMode.select
import org.squeryl.PrimitiveTypeMode.string2ScalarString
import org.squeryl.PrimitiveTypeMode.typedExpression2OrderByArg
import models.Permission._

case class User(id: Long, name: String, email: String, password: String, permission: Permission) extends IdEntity {
  
  def this() = this(Entity.UnpersistedId, "", "", "", Permission.Administrator)
  
  /**
   * Returns default TimeZone for the User. May be implemented as a persisted property in the future.
   */
  def timeZone = TimeZone.getTimeZone("US/Eastern")
}

object User extends Dao[User](NewsSchema.userTable){

  def authenticate(email: String, password: String): Option[User] = inTransaction {
    findByEmail(email).filter { user => BCrypt.checkpw(password, user.password) }
  }

  def findByEmail(email: String): Option[User] = inTransaction {
    this.table.where(user => user.email === email).headOption
  }
  
  def nameById(id: Long) = findById(id).map(user => user.name).getOrElse("")
  
}
