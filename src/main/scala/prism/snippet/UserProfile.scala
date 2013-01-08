package prism.snippet

import xml.NodeSeq
import prism.model.User

class UserProfile {
  def render(in: NodeSeq): NodeSeq = User.profileXhtml()
}
