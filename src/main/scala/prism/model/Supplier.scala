package prism.model

import net.liftweb.mapper._
import net.liftweb.sitemap.{Menu, Loc}
import net.liftweb.sitemap.Loc._
import prism.lib.UserMustBeLoggedIn
import net.liftweb.http.S._

object Supplier extends Supplier
                with LongKeyedMetaMapper[Supplier] {

  val sitemap = List(
    Menu("Supplier") / "suppliers" >> UserMustBeLoggedIn >> HideIfNoKids submenus (
      Menu("List") / "suppliers" / "list",
      Menu("Add") / "suppliers" / "add",
      Menu("View") / "suppliers" / "view" >> Hidden,
      Menu("Edit") / "suppliers" / "edit" >> Hidden
    )
  )

  override def dbTableName = "supplier"
//  override def pageWrapper(body: NodeSeq) = <lift:surround with="default" at="content"></lift:surround>
//  override def calcPrefix = List("admin", _dbTableNameLC)
//  override def displayName = "Supplier"
//  override def showAllMenuLocParams = LocGroup("admin") :: Nil
//  override def createMenuLocParams = LocGroup("admin") :: Nil

}

class Supplier extends LongKeyedMapper[Supplier] with IdPK with CreatedUpdated with OneToMany[Long, Supplier] {
  def getSingleton = Supplier
  object name extends MappedString(this, 150)
  object telephone extends MappedString(this, 30)
  object email extends MappedEmail(this, 200)
  object address extends MappedText(this)
  object openingHours extends MappedString(this, 255)
}
