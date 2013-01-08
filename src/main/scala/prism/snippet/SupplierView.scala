package prism.snippet

import net.liftweb._
import http._
import mapper.MaxRows
import mapper.StartAt
import mapper.{MaxRows, StartAt}
import SHtml._
import sitemap.Loc
import util._
import Helpers._

import scala.xml.{NodeSeq, Text}
import prism.model.Supplier
import common.{Box, Logger}
import prism.lib._
import xml.Text


class SupplierView extends PaginatorSnippet[Supplier] {
  override def itemsPerPage = Props.getInt("table.itemsPerPage").openOr(20)
  override def count = Supplier.count
  override def page = Supplier.findAll(StartAt(curPage*itemsPerPage), MaxRows(itemsPerPage))


  def tableHeader(): NodeSeq = columns.map(column => <th>{column}</th>)

  /**
   * Override the pageXml so that the <li><a /></li> is rendered instead of just a <a />. This also allows the
   * <li /> to have the disabled class.
   *
   * @param ns The link text, if the offset is valid and not the current offset; or, if that is not the case, the static unlinked text to display
   */
  def linkXml(newFirst: Long, ns: NodeSeq, cssClass: String): NodeSeq =
    if(first==newFirst || newFirst < 0 || newFirst >= count)
      <li class={Styles.disabled}><a href="#"><span class={cssClass}></span></a></li>
    else
      <li><a href={pageUrl(newFirst)}><span class={cssClass}></span></a></li>


  /**
   * This is the method that binds template XML according according to the specified configuration.
   * You can reference this as a snippet method directly in your template; or you can call it directly
   * as part of your binding code.
   *
   * Override so that the <a /> link class which is the icon of the link can be passed to the construction of the linkXml instead of pageXml
   * Also make the bind options HTML5 parser compliant
   */
  override def paginate(xhtml: NodeSeq) = {
    bind(navPrefix, xhtml,
      "first" -> linkXml(0, firstXml, S.?("paginator.first")),
      "prev" -> linkXml(first-itemsPerPage max 0, prevXml, S.?("paginator.prev")),
      "allpages" -> {(n:NodeSeq) => pagesXml(0 until numPages, n)},
      "zoomedpages" -> {(ns: NodeSeq) => pagesXml(zoomedPages, ns)},
      "next" -> linkXml(first+itemsPerPage min itemsPerPage*(numPages-1) max 0, nextXml, S.?("paginator.next")),
      "last" -> linkXml(itemsPerPage*(numPages-1), lastXml, S.?("paginator.last")),
      "records" -> currentXml,
      "records_from" -> Text(recordsFrom),
      "records_to" -> Text(recordsTo),
      "records_count" -> Text(count.toString)
    )
  }

  def tableRow(in: NodeSeq): NodeSeq = page.flatMap(item =>
    bind(
      "item", in,
      "id" -> item.id.get,
      "name" -> item.name.get,
      "email" -> item.email.get,
      "telephone" -> item.telephone.get,
      "address" -> item.address.get,
      "opening_hours" -> item.openingHours.get,
      "toolbar" -> ToolbarBlock(item)
    )
  )

  def tableHead(in: NodeSeq): NodeSeq =
    bind(
      "item", in,
      "id" -> S.?("supplier.id"),
      "name" -> S.?("supplier.name"),
      "email" -> S.?("supplier.email"),
      "telephone" -> S.?("supplier.telephone"),
      "address" -> S.?("supplier.address"),
      "opening_hours" -> S.?("supplier.openingHours")
    )

  def columns = List("id","name", "email", "telephone", "address", "openingHours")

  def columnNames = columns.map(col => S.?("supplier." + col))

  def dataSource = Supplier.findAll().map(s => List(s.id.toString(), s.name.get, s.email.get, s.telephone.get, s.address.get, s.openingHours.get))

  def dataTable = {
    val function = (params: DataTableParams) => {
      val data = dataSource

      //("DT_RowId", "rowid_" + r._1))
      new DataTableArraySource(data.size, data.size, data)
    }

    DataTable(
      columnNames,
      function,
      "my-table", // html table id
      List(("bFilter", "false")), // datatables configuration
      ("class", "table table-striped table-bordered")) // set css class for table
  }

  def view(form: NodeSeq):NodeSeq = doViewBind(form, Supplier.find(S.param("id").openOr(-1)))

  def edit:NodeSeq = S.param("id").openOr(-1) match {
    case -1 => (<div>Incorrect id</div>)
    case x  => (<div>{x}</div>)
  }

  def add(form: NodeSeq) = {
    val supplier = Supplier.create

    def checkAndSave(): Unit =
      supplier.validate match {
        case Nil => supplier.save() ; S.notice("Added supplier" + supplier.name)
        case xs => S.error(xs) ; S.mapSnippet("SupplierView.add", doBind)
      }

    def doBind(form: NodeSeq) =
      bind("supplier", form,
        "name" -> supplier.name.toForm,
        "email" -> supplier.email.toForm,
        "address" -> supplier.address.toForm,
        "telephone" -> supplier.telephone.toForm,
        "openingHours" -> supplier.openingHours.toForm,
        "submit" -> submit("New", checkAndSave))

    doBind(form)
  }

  def doViewBind(form: NodeSeq, model: Box[Supplier]): NodeSeq = {
    val supplier = model.openOr(S.redirectTo(S.referer openOr "/"))

    bind("supplier", form,
      "name" -> supplier.name.get,
      "email" -> supplier.email.get,
      "address" -> supplier.address.get,
      "telephone" -> supplier.telephone.get,
      "openingHours" -> supplier.openingHours.get
    )
  }

  def doEditBind(form: NodeSeq, supplier: Supplier): NodeSeq =
    bind("supplier", form,
      "name" -> supplier.name.get,
      "email" -> supplier.email.get,
      "address" -> supplier.address.get,
      "telephone" -> supplier.telephone.get,
      "openingHours" -> supplier.openingHours.get
    )
}

class BootstrapMenu extends Logger {

  def breadcrumb = "*" #> {
    val breadcrumbs: List[Loc[_]] =
      for {
        currentLoc <- S.location.toList
        loc <- currentLoc.breadCrumbs
      } yield loc
    "li *" #> breadcrumbs.map{
      loc => ".link *" #> loc.title &
        ".link [href]" #> loc.createDefaultLink.getOrElse(Text("#"))
      }
  }

  def cssClass(in: NodeSeq): String = cssClassFromText(in.text)

  def cssClassFromText(text: String): String = text match {
    case "Dashboard" => S.?("icon.dashboard")
    case "Supplier" => S.?("icon.suppliers")
    case "Client" => S.?("icon.clients")
    case "Profile" => S.?("icon.settings")
    case "Product" => S.?("icon.products")
    case "Event" => S.?("icon.events")
    case "Article" => S.?("icon.articles")
    case "Gallery" => S.?("icon.gallery")
    case _ => S.?("icon.none")
  }

  def render(in: NodeSeq): NodeSeq = {
    val menuEntries =
      (for {sm <- LiftRules.siteMap; req <- S.request} yield sm.buildMenu(req.location).lines) openOr Nil

    <ul>
      {
      for (item <- menuEntries) yield {
        var styles = item.cssClass openOr ""

        if (item.current) styles += " current"

        item.kids match {
          case Nil =>
            <li><a class="no-submenu" href={item.uri}>{item.text}</a></li>

          case kids =>
            <li>
              <a href="#"><span class={cssClass(item.text)}></span>{item.text}</a>
              <ul>
                {
                for (kid <- kids) yield {
                  <li><a href={kid.uri}>{kid.text}</a></li>
                }
                }
              </ul>
            </li>
        }
      }
      }
    </ul>

  }

}