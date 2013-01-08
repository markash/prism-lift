package code.snippet

import xml.{NodeSeq, Unparsed}

class Html5Shiv {
  def render(in: NodeSeq) = Unparsed("""<!--[if IE 8]>    <html class="no-js ie8 ie" lang="en"> <![endif]-->
                          |<!--[if IE 9]>    <html class="no-js ie9 ie" lang="en"> <![endif]-->
                          |<!--[if gt IE 9]><!--> <html class="no-js" lang="en"> <!--<![endif]-->""")
}
