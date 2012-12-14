/*
 * Copyright (C) 2012 Savings.com, Inc. All rights reserved.
 */
package models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

/**
 * @author kip
 */
class TagContentTest extends Specification {
  
  "TagReplacement" should {
    "replace repeated tags in text" in {
      val tag = "{firstname}"
      val str = tag + " is the worst bowler. " + tag + " really sucks."
      val tagReplacement = TagReplacement(tag, "Kip")
      tagReplacement.replace(str) must equalTo("Kip is the worst bowler. Kip really sucks.")
    }
  }
  
}
