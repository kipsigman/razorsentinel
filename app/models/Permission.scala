package models

/**
 * Enum representing the permission scheme for the app.
 * 
 * @author kip
 */
object Permission extends Enumeration {
  
  type Permission = Value
  val Administrator = Value(1, "Administrator")
  
  
  def displayValues: List[(String, String)] = {
    this.values.toList.map(p => (p.toString, p.toString))
  }
}