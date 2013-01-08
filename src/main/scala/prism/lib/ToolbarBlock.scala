package prism.lib

import xml.NodeSeq
import net.liftweb.http.S
import net.liftweb.mapper.IdPK

/**
 * Generates the link toolbar
 */
object ToolbarBlock extends Function1[IdPK, NodeSeq] {
  def apply(in: IdPK): NodeSeq = (
    <div class="btn-group">
      {linkXml("view", in)}
      {linkXml("edit", in)}
      {linkXml("delete", in)}
    </div>
  )

  /**
   * Generates the link node
   * @param item Whether the link is view, edit, or delete
   */
  def linkXml(item: String, in: IdPK): NodeSeq = (
    <a href={link(item, in)} class={S.?("btn.flat")} title={S.?("toolbar."+item+".title")}><span class={S.?("toolbar."+item)}></span></a>
  )

  def link(item: String, in: IdPK): String = "/suppliers/" + item + "?id=" + in.primaryKeyField
}
