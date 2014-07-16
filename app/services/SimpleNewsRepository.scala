package services

import javax.inject._

import scala.concurrent.Future

import models._

@Singleton()
class SimpleNewsRepository {
  
  def delete[T<:IdEntity](id :Long): Unit = {
    // TODO: Implement
  }
  
  def getById[T<:IdEntity](id: Long): Future[Option[T]] = {
    // TODO: Implement
    Future.successful(None)
  }
  
  def getAll[T<:IdEntity]: Future[Seq[T]] = {
    // TODO: Implement
    Future.successful(Seq[T]())
  }
  
  def save[T<:IdEntity](entity: T): Future[T] = {
    // TODO: Implement
    Future.successful(entity)
  }
}