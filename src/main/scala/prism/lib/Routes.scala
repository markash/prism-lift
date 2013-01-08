package prism.lib

/**
 * A simple url routes object
 */
class AbstractRoute(home: String, basePath: String, suffix: String) {
  val sep = "/"

  def get : String = home match {
    case "/" => home + basePath + sep + suffix
    case _ => home + sep + basePath + sep + suffix
  }
}

case class Route(home: String, basePath: String, suffix: String) extends AbstractRoute(home: String, basePath: String, suffix: String)

object Routes {
  protected val home = "/"
  protected val baseUserManagement = "user_mgt"

  protected val userLoginSuffix = "login"
  protected val userLogoutSuffix = "logout"
  protected val userLostPasswordSuffix = "lost_password"
  protected val userResetPasswordSuffix = "reset_password"
  protected val userChangePasswordSuffix = "change_password"
  protected val userEditSuffix = "edit"
  protected val userValidateSuffix = "validate_user"
  protected val userSignupSuffix = "sign_up"

  val userLogin = Route(home, baseUserManagement, userLoginSuffix)
  val userLogout = Route(home, baseUserManagement, userLogoutSuffix)
  val userLostPassword = Route(home, baseUserManagement, userLostPasswordSuffix)
  val userResetPassword = Route(home, baseUserManagement, userResetPasswordSuffix)
  val userChangePassword = Route(home, baseUserManagement, userChangePasswordSuffix)
  val userEdit = Route(home, baseUserManagement, userEditSuffix)
  val userValidate = Route(home, baseUserManagement, userValidateSuffix)
  val userSignup = Route(home, baseUserManagement, userSignupSuffix)

}
