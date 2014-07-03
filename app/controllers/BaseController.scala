package controllers

import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc.Controller
import play.api.mvc.RequestHeader
import play.api.mvc.Results

/**
 * Superclass for controllers in the Grocery app. Mixes in Authorization/authentication
 * and other app specific features.
 * 
 * @author kip
 */
trait BaseController extends Controller with AuthElement with AuthConfigImpl {
  
  def notFound(implicit request: RequestHeader) = NotFound(views.html.notFound())
  
}