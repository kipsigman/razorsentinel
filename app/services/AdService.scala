package services

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

import javax.inject.Inject
import javax.inject.Singleton
import org.slf4j.LoggerFactory

import controllers.routes
import models.Ad
import models.AdBanner
import models.AdBanner.Style

@Singleton
class AdService @Inject() (implicit ec: ExecutionContext) {
  
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  private val adBannerInventory: Seq[AdBanner] = Seq(
    AdBanner(Option(1), "http://www.darkestdungeon.com", "dd-300x250-1.jpg", Style.MediumRectangle),
    AdBanner(Option(2), "http://www.darkestdungeon.com", "dd-300x250-2.jpg", Style.MediumRectangle),
    AdBanner(Option(3), "http://www.darkestdungeon.com", "dd-300x250-3.jpg", Style.MediumRectangle),
    AdBanner(Option(4), "http://www.darkestdungeon.com", "dd-300x250-4.jpg", Style.MediumRectangle),
    AdBanner(Option(5), "http://www.darkestdungeon.com", "dd-300x250-5.png", Style.MediumRectangle),
    AdBanner(Option(6), "http://www.darkestdungeon.com", "dd-728x90-1.png", Style.Leaderboard)
  )
  
  private val adInventory: Seq[Ad] = adBannerInventory
  
  private lazy val leaderboardInventory: Seq[AdBanner] = adBannerInventory.filter(_.style == Style.Leaderboard)
  private lazy val mediumRectangleInventory: Seq[AdBanner] = adBannerInventory.filter(_.style == Style.MediumRectangle)
  
  private def selectRandomBanner(adBanners: Seq[AdBanner]): AdBanner = {
    val index = Random.nextInt(adBanners.size)
    adBanners(index)
  }
  
  def findAd(id: Int): Future[Option[Ad]] = {
    Future.successful(adInventory.find(_.id.get == id))
  }
  
  def findAdBanner(id: Int): Future[Option[AdBanner]] = {
    Future.successful(adBannerInventory.find(_.id.get == id))
  }
  
  def getBanner(style: Style): AdBanner = {
    val banners = style match {
      case Style.Leaderboard => leaderboardInventory
      case Style.MediumRectangle => mediumRectangleInventory
      case _ => adBannerInventory.filter(_.style == style)
    }
    selectRandomBanner(banners)
  }
  
  def imageUrl(adBanner: AdBanner): String = routes.Assets.at(s"images/banners/${adBanner.filename}").toString
  
  def trackClick(ad: Ad): Unit = {
    logger.debug(s"click ad=${ad.id.get}")
  }
  
  def trackImpression(ad: Ad): Unit = {
    logger.debug(s"impression ad=${ad.id.get}")
  }
}