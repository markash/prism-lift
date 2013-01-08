package prism.snippet

import net.liftweb.http.S

/**
 * Renders the application title
 */
object ApplicationTitle {
  def render = <title>{S.?("application.title")}</title>
}
