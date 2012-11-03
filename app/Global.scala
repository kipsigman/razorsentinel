import org.squeryl.adapters.H2Adapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api.{Application, GlobalSettings}
import org.squeryl.adapters.MySQLAdapter

/**
 * @author kip
 */
object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    
    // Squeryl SessionFactory
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DB.getConnection()(app), new MySQLAdapter) )
  }

}