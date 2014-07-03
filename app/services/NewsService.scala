package services

import models._

trait NewsService {
  
  def findByEmail(email: String): Option[User]

}