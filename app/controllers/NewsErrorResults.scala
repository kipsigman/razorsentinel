package controllers

import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.twirl.api.Html

import kipsigman.play.auth.entity.User
import kipsigman.play.auth.mvc.DefaultErrorResults

trait NewsErrorResults extends DefaultErrorResults {
  
  override protected def notAuthenticatedView(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.error.fourOhOne()
  
  override protected def notAuthorizedView(message: Option[String] = None)(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.error.fourOhThree()
  
  override protected def notFoundView(implicit request: RequestHeader, user: Option[User]): Html =
    views.html.error.fourOhFour()
  
}