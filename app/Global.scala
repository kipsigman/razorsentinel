import org.squeryl.adapters.{H2Adapter,MySQLAdapter}
import org.squeryl.{Session, SessionFactory}
import org.squeryl.PrimitiveTypeMode._
import play.api.db.DB
import play.api.{Application, GlobalSettings}
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play
import models.NewsSchema

/**
 * @author kip
 */
object Global extends GlobalSettings {
  
  override def onStart(app: Application) {

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

}