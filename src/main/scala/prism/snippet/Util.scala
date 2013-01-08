package prism.snippet

import scala.xml.{NodeSeq}
import prism.model.User

class Util {
  def loggedIn(html: NodeSeq) =
    if (User.loggedIn_?) html else NodeSeq.Empty

  def loggedOut(html: NodeSeq) =
    if (!User.loggedIn_?) html else NodeSeq.Empty
}