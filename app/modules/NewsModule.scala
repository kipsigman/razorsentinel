package modules

import com.google.inject.AbstractModule
import com.typesafe.config.Config

import play.api.Configuration
import play.api.Environment

import models.NewsRepository
import models.slick.NewsRepositorySlick
import services.ContentAuthorizationService
import services.UrlService

class NewsModule(
    environment: Environment,
    configuration: Configuration) extends AbstractModule {

  def configure() = {
    val config = configuration.underlying
    bind(classOf[Config]).toInstance(config)
    bind(classOf[NewsRepository]).to(classOf[NewsRepositorySlick])
    bind(classOf[ContentAuthorizationService])
    bind(classOf[UrlService])
  }
}