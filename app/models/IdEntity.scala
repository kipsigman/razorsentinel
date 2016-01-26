package models

import play.api.db.slick._

trait IdEntity {
  def id: Option[Int]
}