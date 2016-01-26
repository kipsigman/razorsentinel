package models.slick

import slick.driver.JdbcProfile

import models.IdEntity

trait TableDefinitions {
  protected val driver: JdbcProfile
  import driver.api._

  abstract class IdTable[T <: IdEntity](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  }
}