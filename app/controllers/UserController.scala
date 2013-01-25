package controllers

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{FormError, Forms, Mapping}
import play.api.mvc._


import models._
import models.Permission._

/**
 * @author kip
 */
object UserController extends SecureController {
  
  val newUserForm = Form(
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText.verifying("Password must be at least 6 characters", _.length() >= 6),
      "permission" -> enum(Permission)
    )
    // Custom binding
    { (id, name, email, password, permission) =>
      {
        // New Entity
        User(Entity.UnpersistedId, name, email, password, permission)
      }
    }
    // Custom unbinding
    {User.unapply}
  )
  
  
  val editUserForm = Form(
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "email" -> email,
      "permission" -> enum(Permission)
    )
    // Custom binding
    { (id, name, email, permission) =>
      {
        val user = User.findById(id).get
        user.copy(name = name, email = email, permission = permission)
      }
    }
    // Custom unbinding
    {user => Some(user.id, user.name, user.email, user.permission) }
  )
  
  def list = authorizedAction(Administrator) { implicit user => implicit request =>

  	val users: Seq[User] = User.findAll
    Ok(views.html.user.list(users))
  }
  
  def create = authorizedAction(Administrator) { implicit user => implicit request =>

    Ok(views.html.user.create(newUserForm.fill(new User())))
  }
  
  def edit(id: Long) = authorizedAction(Administrator) { implicit user => implicit request =>
    
    Ok(views.html.user.edit(id, editUserForm.fill(User.findById(id).get)))
  }
  
  /**
   * Handles both saving a NEW user
   */
  def save = authorizedAction(Administrator) { implicit user => implicit request =>
  	
    def hashPW(user: User) = user.copy(password=BCrypt.hashpw(user.password, BCrypt.gensalt()))
    
    newUserForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.user.create(formWithErrors)),      
      myUser => {
        User.save(hashPW(myUser))
        Redirect(routes.UserController.list()).flashing("success" -> "User created!")
      }
    )
  } // end save
  
  
  /**
   * Handles saving an EXISTING user
   */
  def update(id: Long) = authorizedAction(Administrator) { implicit user => implicit request =>

    editUserForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.user.edit(id, formWithErrors)),      
      myUser => {
        User.save(myUser)
        Redirect(routes.UserController.list()).flashing("success" -> "User updated!")
      }
    )
  } // end save
  
  /**
   * COPIED FROM https://github.com/leon/play-enumeration
   * 
   * Constructs a simple mapping for a text field (mapped as `scala.Enumeration`)
   *
   * For example:
   * {{{
   *   Form("status" -> enum(Status))
   * }}}
   *
   * @param enum the Enumeration#Value
   */
  def enum[E <: Enumeration](enum: E): Mapping[E#Value] = Forms.of(enumFormat(enum))

  /**
   * Default formatter for `scala.Enumeration`
   *
   */
  def enumFormat[E <: Enumeration](enum: E): Formatter[E#Value] = new Formatter[E#Value] {
    def bind(key: String, data: Map[String, String]) = {
      play.api.data.format.Formats.stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[E#Value]
          .either(enum.withName(s))
          .left.map(e => Seq(FormError(key, "Invalid value", Nil)))
      }
    }
    def unbind(key: String, value: E#Value) = Map(key -> value.toString)
  }

}

