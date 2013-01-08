package prism.lib

import xml.NodeSeq
import prism.model.User
import net.liftweb.common.{Box, Empty}
import net.liftweb.http.S

/**
 * Creates the NodeSeq that represenets a user profile box which is empty when the Box[TheUserType] is
 * empty.
 */
object ProfileBlock extends Function1[Box[User.TheUserType], NodeSeq] {
  def apply(currentUser: Box[User.TheUserType]): NodeSeq = currentUser match {
    case Empty =>
      NodeSeq.Empty
    case _ =>
        val user = currentUser.open_!
        (
        <section class={Styles.section_user_profile}>
          <figure>
            <img alt={user.firstName.get + " " + user.lastName.get + " avatar"} src="http://placekitten.com/50/50" />
            <figcaption>
              <strong><a href="#" class="">{user.firstName.get} {user.lastName.get}</a></strong>
              <em>{if (user.superUser) "Administrator" else "User"}</em>
              <ul>
                <li><a class={Styles.btn_primary_flat} href={Routes.userEdit.get} title={S.?("form.profile.edit.btn.title")}>{S.?("form.profile.edit.btn.text")}</a></li>
                <li><a class={Styles.btn_primary_flat} href={Routes.userLogout.get} title={S.?("form.profile.logout.btn.title")}>{S.?("form.profile.logout.btn.text")}</a></li>
              </ul>
            </figcaption>
          </figure>
        </section>
        )
  }
}
