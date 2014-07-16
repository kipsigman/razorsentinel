package services

import scala.concurrent.Future
import models._

trait NewsService {
  
  def delete[T<:IdEntity](id :Long): Unit
  
  def findById[T<:IdEntity](id: Long): Future[Option[T]]
  
  def findAll[T<:IdEntity]: Future[Seq[T]]
  
  def save[T<:IdEntity](entity: T): Future[T]

}