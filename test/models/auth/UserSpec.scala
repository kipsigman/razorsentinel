package models.auth

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class UserSpec extends Specification with models.TestData {
  
  "hasRole" should {
    "return true if Role is in Set" in {
      user.copy(roles = Set(Role.Editor)).hasRole(Role.Editor) must beTrue
      user.copy(roles = Set(Role.Editor, Role.Member)).hasRole(Role.Member) must beTrue
    }
    
    "return false if Role is not in Set" in {
      user.hasRole(Role.Editor) must beFalse
      user.hasRole(Role.Administrator) must beFalse
      user.copy(roles = Set(Role.Editor)).hasRole(Role.Member) must beFalse
      user.copy(roles = Set(Role.Editor, Role.Member)).hasRole(Role.Administrator) must beFalse
    }
    
    "return true for all Roles if Administrator Role exists" in {
      user.copy(roles = Set(Role.Administrator)).hasRole(Role.Administrator) must beTrue
      user.copy(roles = Set(Role.Administrator)).hasRole(Role.Editor) must beTrue
      user.copy(roles = Set(Role.Administrator, Role.Editor)).hasRole(Role.Member) must beTrue
    }
     
  }

}