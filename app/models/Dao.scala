package models

import scala.language.postfixOps
import org.squeryl.PrimitiveTypeMode.__thisDsl
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.orderByArg2OrderByExpression
import org.squeryl.PrimitiveTypeMode.select
import org.squeryl.PrimitiveTypeMode.typedExpression2OrderByArg
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
