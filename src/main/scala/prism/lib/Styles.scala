package prism.lib

import net.liftweb.http.S

/**
 * A convenient class that represents the CSS styles
 */
object Styles {
  lazy val section_user_profile = S.?("section.user.profile")
  lazy val btn_primary = S.?("btn.primary")
  lazy val btn_primary_flat = S.?("btn.primary.flat")

  lazy val enabled = S.?("enabled")
  lazy val disabled = S.?("disabled")
}
