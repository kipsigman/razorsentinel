package controllers

import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.DefaultErrorResults
import play.api.mvc.RequestHeader
import play.twirl.api.Html

trait ErrorResults extends DefaultErrorResults {
  override protected def notAuthenticatedView(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.error.fourOhOne()
  
  override protected def notAuthorizedView(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.error.fourOhThree()
  
  override protected def notFoundView(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.error.fourOhFour()
    
  override protected def serverErrorView(exception: Throwable)(implicit request: RequestHeader, user: Option[User]): Html = {
    views.html.error.fiveZeroZero(exception)
  }
}