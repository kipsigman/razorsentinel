package modules

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.Configuration
import play.api.Environment

import models.ModelRepository
import models.slick.ModelRepositorySlick
import services.ContentAuthorizationService
import kipsigman.play.service.HtmlService

class AppModule(
    environment: Environment,
    configuration: Configuration) extends AbstractModule {

  def configure() = {
    val config = configuration.underlying
    bind(classOf[Config]).toInstance(config)
    bind(classOf[ModelRepository]).to(classOf[ModelRepositorySlick])
    bind(classOf[ContentAuthorizationService])
    bind(classOf[HtmlService])
  }
}