package models

import java.sql.Timestamp
import org.squeryl.KeyedEntity

/**
 * Entity traits for use with Squeryl classes.
 * 
 * @author kip
 */
trait IdEntity extends KeyedEntity[Long] {
  
  def id: Long
  
  override def isPersisted = id > 0
}

object Entity {
  
  val UnpersistedId: Long = -1
}