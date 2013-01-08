package prism.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import net.liftweb.http.{RedirectResponse, SHtml, S}
import net.liftweb.http.js.JsCmds.FocusOnLoad
import net.liftweb.sitemap.Loc.{Template, If, LocParam}
import xml.{Elem, Node, NodeSeq}
import xml.transform.{RewriteRule, RuleTransformer}
import prism.lib.{ProfileBlock}

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"

  def loginWrap = Full(<lift:surround with="login" at="content"><lift:bind /></lift:surround>)
  override def screenWrap = Full(<lift:surround with="default" at="content"><lift:bind /></lift:surround>)

  override def fieldOrder = List(id, firstName, lastName, email, locale, timezone, password, textArea)

  override def skipEmailValidation = true

  override protected def loginMenuLocParams: List[LocParam[Unit]] =
    If(notLoggedIn_? _, () => RedirectResponse("/")) ::
      Template(() => wrapLogin(login)) ::
      Nil


  override def loginXhtml = {
    (
      <div class="login-container">
        <form method="post" action={S.uri}>
        <fieldset>
            <div class="control-group"><label class="control-label" for="login">Email</label>
              <div class="controls"><user:email class="large"/></div>
            </div>
            <div class="control-group"><label class="control-label" for="password">Password</label>
              <div class="controls"><user:password class="large"/></div>
            </div>
            <div class="control-group"><span />
              <div class="controls"><a href={lostPasswordPath.mkString("/", "/", "")}>Password recovery</a></div>
            </div>
            <div class="form-actions">
              <user:submit class="right" />
            </div>
        </fieldset>
      </form>
    </div>
   )
  }

  override def editXhtml(user: TheUserType) = {
    (
      <div class="row-fluid">
        <article class="span12 data-block nested">
          <div class="data-container">
            <header>
              <h3>{S.?("form.user.edit")}</h3>
            </header>
            <section>
              <form method="post" action={S.uri}>
                <fieldset>
                  {localForm(user, true, editFields)}
                  <div class="form-actions">
                    <user:submit class={S.?("form.submit.primary.css")} />
                  </div>
                </fieldset>
              </form>
            </section>
          </div>
        </article>
      </div>
    )
  }

  override def login = {
    if (S.post_?) {
      S.param("username").
        flatMap(username => findUserByUserName(username)) match {
        case Full(user) if user.validated_? &&
          user.testPassword(S.param("password")) => {
          val preLoginState = capturePreLoginState()
          val redir = loginRedirect.is match {
            case Full(url) =>
              loginRedirect(Empty)
              url
            case _ =>
              homePage
          }

          logUserIn(user, () => {
            S.notice(S.?("logged.in"))

            preLoginState()

            S.redirectTo(redir)
          })
        }

        case Full(user) if !user.validated_? =>
          S.error(S.?("account.validation.error"))

        case _ => S.error(S.?("invalid.credentials"))
      }
    }

    bind("user", loginXhtml,
      "email" -> (FocusOnLoad(<input type="text" name="username" class="input-xlarge"/>)),
      "password" -> (<input type="password" name="password" class="input-xlarge"/>),
      "submit" -> loginSubmitButton("Login"))
  }

  def profileXhtml() = ProfileBlock(currentUser)

  def emailField() = standardTextField("username", "large")
  def passwordField() = standardPasswordField("password", "large")
  def firstNameField() = standardTextField("firstName", "large")
  def lastNameField() = standardTextField("lastName", "large")

  override def standardSubmitButton(name: String,  func: () => Any = () => {}) = {
    SHtml.submit(name, func, "class" -> S.?("form.submit.primary.css"))
  }

  def standardTextField(name: String, cssClass: String) = {
    (<input type="text" name={name} class={cssClass} />)
  }

  def standardPasswordField(name: String, cssClass: String) = {
    (<input type="" name={name} class={cssClass} />)
  }


  protected def wrapLogin(in: NodeSeq): NodeSeq =
    loginWrap.map(new RuleTransformer(new RewriteRule {
      override def transform(n: Node) = n match {
        case e: Elem if "bind" == e.label && "lift" == e.prefix => in
        case _ => n
      }
    })) openOr in

  protected def localFormRow(displayName: String, field: NodeSeq): NodeSeq = {
    (
      <div class="control-group"><label>{displayName}</label></div>
      <div class="controls">{field}</div>
    )
  }

  override protected def localForm(user: TheUserType, ignorePassword: Boolean, fields: List[FieldPointerType]): NodeSeq = {
    (
    for {
      pointer <- fields
      field <- computeFieldFromPointer(user, pointer).toList
      if field.show_? && (!ignorePassword || !pointer.isPasswordField_?)
      form <- field.toForm.toList
    } yield localFormRow(field.displayName, form)
    ).foldLeft(NodeSeq.Empty)((x, y) => x++y)
  }


}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}
