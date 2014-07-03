package models

import scala.language.postfixOps
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Table

abstract class Dao[T<:IdEntity](val table: Table[T]) {
  
  def delete(id :Long): Unit = inTransaction {
    this.table.deleteWhere(_.id === id)
  }
  
  def findById(id: Long): Option[T] = inTransaction {table.lookup(id)}
  
  def findAll: List[T] = inTransaction {
    from(table) {
      entity => select(entity) orderBy(entity.id asc)
    }.toList
  }
  
  def save(entity: T) = inTransaction {
    table.insertOrUpdate(entity)
  }
  
} // end Dao
