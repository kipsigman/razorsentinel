package models

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.CategoryOptions

object NewsCategoryOptions extends CategoryOptions {
  val Arts = Category(None, "arts", 9)
  val Business = Category(None, "business", 4)
  val Entertainment = Category(None, "entertainment", 6)
  val Food = Category(None, "food", 10)
  val Health = Category(None, "health", 8)
  val Local = Category(None, "local", 1)
  val National = Category(None, "national", 2)
  val ScienceAndTechnology = Category(None, "science-and-technology", 5)
  val Sports = Category(None, "sports", 7)
  val TopStories = Category(None, "top-stories", 0)
  val Travel = Category(None, "travel", 11)
  val World = Category(None, "world", 3)
  
  override val all: Set[Category] = Set(
    Arts,
    Business,
    Entertainment,
    Food,
    Health,
    Local,
    National,
    ScienceAndTechnology,
    Sports,
    TopStories,
    Travel,
    World
  )
}