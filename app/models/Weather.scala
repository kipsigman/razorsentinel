package models

import kipsigman.domain.entity.Location

case class Weather(
  code: Int,
  description: String,
  tempCelcius: Int,
  tempFarenheit: Int,
  location: Location)