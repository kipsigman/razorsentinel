package services

import javax.inject._

import scala.concurrent.Future

import models._

@javax.inject.Singleton
class SimpleNewsService @javax.inject.Inject()(repository:SimpleNewsRepository) extends NewsService {

  def delete[T<:IdEntity](id :Long): Unit = repository.delete(id)
  
  def findById[T<:IdEntity](id: Long): Future[Option[T]] = repository.getById(id)
  
  def findAll[T<:IdEntity]: Future[Seq[T]] = repository.getAll
  
  def save[T<:IdEntity](entity: T): Future[T] = repository.save(entity)
}