package services

import javax.inject._

import models._

@javax.inject.Singleton
class SimpleNewsService @javax.inject.Inject()(repository:SimpleNewsRepository) extends NewsService {

  def findByEmail(email: String): Option[User] = None
  
}