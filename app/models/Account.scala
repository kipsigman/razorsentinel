package models

case class Account(id: Long, email: String, password: String, name: String, permission: Permission)

object Account {
  
  val users = Set(
    Account(1L, "kip.sigman@gmail.com", "password1", "kipperdog", Administrator),
    Account(2L, "madnapper@gmail.com", "password1", "madnapper", NormalUser)
  )
  
  def findById(id: Long) = this.users.find(_.id == id)
  def authenticate(email: String, password: String) = this.users.find((user: Account) => user.email == email && user.password == password)
  
}