package models.slick

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait SilhoutteDBConfig extends SilhoutteTableDefinitions with HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig: DatabaseConfig[JdbcProfile]
}