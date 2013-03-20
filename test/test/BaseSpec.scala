/*
 * Copyright (C) 2012 Savings.com, Inc. All rights reserved.
 */

package test

import models._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.test.FakeApplication


/**
 * Base test, used for configuration.
 * 
 * @author kip
 */
trait BaseSpec {
  
  def fakeApp = FakeApplication(additionalConfiguration = inMemoryDatabase() + (("evolutionplugin", "disabled")))
  
}