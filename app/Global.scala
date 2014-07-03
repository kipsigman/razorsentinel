import scala.concurrent.Future

import com.google.inject._

import org.squeryl.adapters.{H2Adapter,MySQLAdapter}
import org.squeryl.{Session, SessionFactory}
import org.squeryl.PrimitiveTypeMode._
import play.api.db.DB
import play.api.{Application, GlobalSettings}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.mvc.Result
import play.api.Play
import controllers.BaseController
import models.NewsSchema
import services._

object Global extends GlobalSettings with BaseController {
  
  private var injector: Injector = _
  
  override def onStart(app: Application) {
    
    // Set up Guice DI
    val playModule = new AbstractModule {
      override def configure(): Unit = {
        bind(classOf[NewsService]).to(classOf[SimpleNewsService])
      }
    }
    val modules = Array(playModule)
    injector = Guice.createInjector(modules: _*)

    // TODO: Deprecate, as Guice will handle these issues.
    // if play is being run in test mode, configure Squeryl to run using the H2 dialect because we want our tests to 
    // run using an in memory database 
    if (Play.isTest(app)) {
      
      // Squeryl SessionFactory
      SessionFactory.concreteFactory = Some(() =>
        Session.create(DB.getConnection()(app), new H2Adapter) )      
      
        // since this is an in-memory database that gets wiped out every time the app is shutdown, 
        // we need to recreate the schema
      inTransaction {
      
        NewsSchema.create
      }
    } else {
    
      // Squeryl SessionFactory
      SessionFactory.concreteFactory = Some(() =>
        Session.create(DB.getConnection()(app), new MySQLAdapter) )
    }
  }
  
  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    implicit val aRequest = request
    implicit val flash = request.flash
    Future(notFound)
  }
  
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    injector.getInstance(controllerClass)
  }
  
}