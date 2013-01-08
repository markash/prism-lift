package prism.lib

import net.liftweb.sitemap.Loc
import prism.model.User
import net.liftweb.http.RedirectResponse

object UserMustBeLoggedIn extends Loc.If(() => User.loggedIn_?, () => RedirectResponse("/user_mgt/login"))