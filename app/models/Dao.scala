package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Table

abstract class Dao[T<:IdEntity](val table: Table[T]) {
  
  def findById(id: Long): Option[T] = inTransaction {table.lookup(id)}
  
  def findAll: List[T] = inTransaction {
    from(table) {
      entity => select(entity) orderBy(entity.id asc)
    }.toList
  }
  
  def save(entity: T) = inTransaction {
    table.insertOrUpdate(entity)
  }
  
}