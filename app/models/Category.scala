package models

import play.api.data.format.Formatter
import play.api.data.FormError
import play.api.data.Forms
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.mvc.QueryStringBindable

sealed abstract class Category(val name: String, val order: Int) {
  override def toString: String = name
}

object Category {
  case object Arts extends Category("Arts", 7)
  case object Business extends Category("Business", 4)
  case object Entertainment extends Category("Entertainment", 8)
  case object Food extends Category("Food", 8)
  case object Health extends Category("Health", 8)
  case object Lifestyle extends Category("Lifestyle", 8)
  case object Local extends Category("Local", 0)
  case object National extends Category("National", 1)
  case object Opinion extends Category("Opinion", 9)
  case object Politics extends Category("Politics", 3)
  case object ScienceTechnology extends Category("ScienceTechnology", 6)
  case object Sports extends Category("Sports", 5)
  case object Travel extends Category("Travel", 8)
  case object World extends Category("World", 2)
  
  val all: Set[Category] = Set(
    Arts,
    Business,
    Entertainment,
    Food,
    Health,
    Lifestyle,
    Local,
    National,
    Opinion,
    Politics,
    ScienceTechnology,
    Sports,
    Travel,
    World
  )
  
  lazy val allSorted: Seq[Category] = all.toSeq.sortBy(c => (c.order, c.name))
  
  def apply(name: String): Category = {
    all.find(s => s.name == name) match {
      case Some(category) => category
      case None => throw new IllegalArgumentException(s"Invalid Category: $name")
    }
  }
  
  implicit def formatter: Formatter[Category] = new Formatter[Category] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Category] = {
      //data.get(key).toRight(Seq(FormError(key, "error.required", Nil)))
      data.get(key) match {
        case Some(name) => try {
          Right(Category(name))
        } catch {
          case e: Exception =>Left(Seq(FormError(key, "error.category.invalid", Nil)))
        }
        case None => Left(Seq(FormError(key, "error.required", Nil)))
      }
    }
    override def unbind(key: String, value: Category): Map[String, String] = {
      Map(key -> value.name)
    }
  }
  
  val formMapping: Mapping[Category] = of[Category]
  
  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[Category] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Category]] = {
        for {
          nameEither <- stringBinder.bind(key, params)
        } yield {
          nameEither match {
            case Right(name) => Right(Category(name))
            case _ => Left("Unable to bind Category")
          }
        }
      }
      override def unbind(key: String, value: Category): String = stringBinder.unbind(key, value.name)
    }
}