package models

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.CategoryOptions

object NewsCategoryOptions extends CategoryOptions {
  // case object Arts extends Category("arts", 7)
  case object Business extends Category("business", 4)
  // case object Entertainment extends Category("entertainment", 8)
  case object Food extends Category("food", 8)
  case object Health extends Category("health", 8)
  case object Lifestyle extends Category("lifestyle", 8)
  case object Local extends Category("local", 0)
  case object National extends Category("national", 1)
  // case object Opinion extends Category("opinion", 9)
  case object Politics extends Category("politics", 3)
  case object ScienceAndTechnology extends Category("science-and-technology", 6)
  case object Sports extends Category("sports", 5)
  // case object Travel extends Category("travel", 8)
  case object World extends Category("world", 2)
  
  override val all: Set[Category] = Set(
    // Arts,
    Business,
    // Entertainment,
    Food,
    Health,
    Lifestyle,
    Local,
    National,
    // Opinion,
    Politics,
    ScienceAndTechnology,
    Sports,
    // Travel,
    World
  )
}