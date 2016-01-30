package modules

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext

import com.google.inject.AbstractModule
import com.google.inject.Provider
import com.google.inject.Provides
import com.google.inject.TypeLiteral
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.EventBus
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.CacheLayer
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.FingerprintGenerator
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.api.util.IDGenerator
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.util.PlayHTTPLayer
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorService
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticatorSettings
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.repositories.DelegableAuthInfoRepository
import com.mohiva.play.silhouette.impl.services.GravatarService
import com.mohiva.play.silhouette.impl.util.BCryptPasswordHasher
import com.mohiva.play.silhouette.impl.util.DefaultFingerprintGenerator
import com.mohiva.play.silhouette.impl.util.PlayCacheLayer
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
import play.api.libs.ws.WSClient

import models.auth.User
import models.auth.UserRepository
import models.auth.slick.PasswordInfoRepositorySlick
import models.auth.slick.UserRepositorySlick
import services.auth.UserService

/**
 * Module for all Auth/User dependencies. 
 * Auth implemented with Silhouette: http://silhouette.mohiva.com/.
 */
class AuthModule(
    environment: play.api.Environment,
    configuration: Configuration) extends AbstractModule {
  
  def configure() {
    bind(classOf[UserRepository]).to(classOf[UserRepositorySlick])
    bind(classOf[UserService])
    bind(new TypeLiteral[DelegableAuthInfoDAO[PasswordInfo]] {}).to(classOf[PasswordInfoRepositorySlick])
    
    bind(classOf[PasswordHasher]).toInstance(new BCryptPasswordHasher)
    
    bind(classOf[FingerprintGenerator]).toInstance(new DefaultFingerprintGenerator(false))
    bind(classOf[Clock]).toInstance(Clock())
    
    bind(new TypeLiteral[AuthenticatorService[CookieAuthenticator]] {}).toProvider(classOf[AuthenticatorServiceProvider])
    
    bind(classOf[EventBus]).toInstance(EventBus())
    bind(new TypeLiteral[Environment[User, CookieAuthenticator]] {}).toProvider(classOf[EnvironmentProvider])
    
    bind(classOf[CacheLayer]).to(classOf[PlayCacheLayer])
  }
  
  @Provides @Singleton
  def provideHTTPLayer(client: WSClient)(implicit ec: ExecutionContext): HTTPLayer = new PlayHTTPLayer(client)
  
  @Provides @Singleton
  def provideAvatarService(httpLayer: HTTPLayer): AvatarService = new GravatarService(httpLayer)
  
  @Provides @Singleton
  def provideAuthInfoRepository(
    passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo])(implicit ec: ExecutionContext): AuthInfoRepository = {

    new DelegableAuthInfoRepository(passwordInfoDAO)
  }
  
  @Provides @Singleton
  def provideCredentialsProvider(
    authInfoRepository: AuthInfoRepository,
    passwordHasher: PasswordHasher)(implicit ec: ExecutionContext): CredentialsProvider = {

    new CredentialsProvider(authInfoRepository, passwordHasher, Seq(passwordHasher))
  }
  
  @Provides @Singleton
  def provideIDGenerator()(implicit ec: ExecutionContext): IDGenerator = {
    new SecureRandomIDGenerator()
  }  
}


@Singleton
class AuthenticatorServiceProvider @Inject() (
  config: Config,
  fingerprintGenerator: FingerprintGenerator,
  idGenerator: IDGenerator,
  clock: Clock)(implicit ec: ExecutionContext) extends Provider[AuthenticatorService[CookieAuthenticator]] {

  override lazy val get: AuthenticatorService[CookieAuthenticator] = {
    val authenticatorConfig = config.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    new CookieAuthenticatorService(authenticatorConfig, None, fingerprintGenerator, idGenerator, clock)
  }
}

@Singleton
class EnvironmentProvider @Inject() (
  userService: UserService,
  authenticatorService: AuthenticatorService[CookieAuthenticator],
  eventBus: EventBus)(implicit ec: ExecutionContext) extends Provider[Environment[User, CookieAuthenticator]] {
  
  override lazy val get: Environment[User, CookieAuthenticator] = {
    Environment[User, CookieAuthenticator](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }
}
