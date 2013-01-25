/*
 * Copyright (C) 2012 Savings.com, Inc. All rights reserved.
 */
package controllers

import jp.t2v.lab.play20.auth.Auth
import play.api.mvc.Controller
import play.api.mvc.Flash
import play.api.mvc.Results

/**
 * Superclass for controllers in the Grocery app. Mixes in Authorization/authentication
 * and other app specific features.
 * 
 * @author kip
 */
trait BaseController extends Controller with Auth with AuthConfigImpl {
  
}