package controllers

import scala.concurrent.ExecutionContext

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator

import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.play.auth.entity.User
import play.api.i18n.MessagesApi

import services.AdService

@Singleton
class AdController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator])(implicit ec: ExecutionContext, adService: AdService) 
  extends BaseController(messagesApi, env) {
  
  def click(id: Int) = UserAwareAction.async {implicit request =>
    adService.findAd(id) map {
      case Some(ad) => {
        adService.trackClick(ad)
        Redirect(ad.clickUrl)
      }
      case None => notFound
    }
  }
  
  def image(id: Int) = UserAwareAction.async {implicit request =>
    adService.findAdBanner(id) map {
      case Some(adBanner) => {
        adService.trackImpression(adBanner)
        Redirect(adService.imageUrl(adBanner))
      }
      case None => notFound
    }
  }
  
}