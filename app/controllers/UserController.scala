package controllers

import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms.email
import play.api.data.Forms.mapping
import play.api.data.Forms.longNumber
import play.api.data.Forms.number
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import se.radley.plugin.enumeration.form.enum
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

}