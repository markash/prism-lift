package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.net.liftweb.util.Mailer.{To, From, PlainMailBodyType, Subject}
import _root_.net.liftweb.util.Mailer
import _root_.java.sql.{Connection, DriverManager}
import prism.model.{User, Supplier, Setting, Settings}
import scala.collection.{ mutable, immutable, generic }


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  lazy val vendor: StandardDBVendor =
    scala.sys.env.get("DATABASE_URL") match {
      case None => new StandardDBVendor(
                        Props.get("db.driver") openOr "org.h2.Driver",
                        Props.get("db.url") openOr "jdbc:h2:lift_proto.db",
                        Props.get("db.user"),
                        Props.get("db.password"))
      case x => initHerokuDb(x.get)
    }

  def initHerokuDb(databaseUrl: String): StandardDBVendor = {
    // Extract credentials from Heroku database URL:
    val uri = new java.net.URI(databaseUrl)
    val Array(user:String, password:String) = uri.getUserInfo.split(":")

    new StandardDBVendor("org.postgresql.Driver", databaseUrl, Full(user), Full(password))  
  }

  def boot {
    
    if (!DB.jndiJdbcConnAvailable_?) {
      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }
    
    // where to search snippet
    LiftRules.addToPackages("prism")
    Schemifier.schemify(true, Schemifier.infoF _, User, Supplier, Setting)

    // Build SiteMap
    val sitemap = List(
      Menu("Dashboard") / ""  submenus (
        Menu("Home") / "index",
        User.loginMenuLoc.open_!,
        User.createUserMenuLoc.open_!,
        Menu(Loc("Static", Link(List("static"), true, "/static/index"), "Static Content"))
      ),
      Menu("Profile") / "profile" >> HideIfNoKids submenus (
        User.logoutMenuLoc.open_!,
        User.editUserMenuLoc.open_!,
        User.changePasswordMenuLoc.open_!
      ),
      Menu("Hidden") / "hidden" >> Hidden submenus (
        User.validateUserMenuLoc.open_!
      )
    )

    /* Sitemap */
    LiftRules.setSiteMap(SiteMap(sitemap++Supplier.sitemap:_*))

    /* Show the spinny image when an Ajax call starts */
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loading").cmd)

    /* Make the spinny image go away when it ends */
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loading").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    /* Resource bundles */
    LiftRules.resourceNames = "icons" :: "forms" :: "messages" :: "styles" :: Nil

    /* HTML5 Parser */
    LiftRules.htmlProperties.default.set( (r: Req) => new Html5Properties(r.userAgent) )

    /* Debugging the request pipeline */
    LiftRules.onBeginServicing.append {
      case r @ Req(List("user_mgt", _), _, _) => println("Users Pipeline: " + r)
      case r @ Req(List("suppliers", _), _, _) => println("Suppliers Pipeline: " + S.param("id") + ":" + r)
      case r => println("Received: "+r)
    }

    Settings.setupEmail

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
