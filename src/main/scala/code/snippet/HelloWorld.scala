package code.snippet 

import java.util.Date
import net.liftweb.util._
import Helpers._


class HelloWorld {

  // replace the contents of the element with id "time" with the date
  def howdy = "#time *" #> new Date().toString

}

