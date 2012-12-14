package controllers

import play.api.mvc.Controller

import jp.t2v.lab.play20.auth.Auth

/**
 * Superclass for controllers using security. Mixes in Authorization/authentication
 * and other app specific features.
 * 
 * @author kip
 */
trait SecureController extends Controller with Auth with AuthConfigImpl