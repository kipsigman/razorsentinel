package models

import kipsigman.domain.entity.IdEntity

trait Ad extends IdEntity {
  def clickUrl: String
}

case class AdBanner(id: Option[Int], clickUrl: String, filename: String, style: AdBanner.Style) extends Ad

object AdBanner {
  abstract class Style(val width: Int, val height: Int)
  object Style {
    // Banners & Buttons
    case object Leaderboard extends Style(728, 90)
    case object Full extends Style(468, 60)
    case object Half extends Style(234, 60)
    case object MicroBar extends Style(88, 31)
    case object Button1 extends Style(120, 90)
    case object Button2 extends Style(120, 60)
    case object SquareButton extends Style(125, 125)
    
    // Rectangles & Pop-ups
    case object Square extends Style(250, 250)
    case object Rectangle extends Style(180, 150)
    case object MediumRectangle extends Style(300, 250)
    case object LargeRectangle extends Style(336, 280)
    case object VerticalRectangle extends Style(240, 400)
    
    // Skyscrapers
    case object Skyscraper extends Style(120, 600)
    case object WideSkyscraper extends Style(160, 600)
  }
}