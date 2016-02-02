package models.auth.slick

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait AuthDBConfig extends AuthTableDefinitions with HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig: DatabaseConfig[JdbcProfile]
}