package models

case class User(id: Long, email: String, password: String) extends IdEntity

object User {
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    if (email == "kip.sigman@gmail.com")
      Some(User(1, "kip.sigman@gmail.com", "1234"))
    else
      None
  }
  
  def authenticate(email: String, password: String): Option[User] = {
    findByEmail(email).filter { user => user.password == password }
  }

}