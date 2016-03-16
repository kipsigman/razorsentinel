package modules

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.Configuration
import play.api.Environment

import models.ModelRepository
import models.slick.ModelRepositorySlick
import services.AdService
import services.ContentAuthorizationService

class AppModule(
    environment: Environment,
    configuration: Configuration) extends AbstractModule {

  def configure() = {
    val config = configuration.underlying
    bind(classOf[Config]).toInstance(config)
    bind(classOf[ModelRepository]).to(classOf[ModelRepositorySlick])
    bind(classOf[AdService])
    bind(classOf[ContentAuthorizationService])
  }
}