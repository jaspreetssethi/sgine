package org.sgine.workflow

import annotation.tailrec

/**
 *
 *
 * @author Matt Hicks <mhicks@sgine.org>
 */
trait Asynchronous extends Workflow {
  override def begin() = {
    super.begin()

    currentItems.foreach(Asynchronous.beginItem)
  }

  override def act(delta: Float) = {
    updateAsync(delta, currentItems)
    currentItems.isEmpty
  }

  @tailrec
  private def updateAsync(delta: Float, items: List[WorkflowItem]): Unit = {
    if (!items.isEmpty) {
      val item = items.head
      if (item.act(delta) || item.finished) {
        item.end()
        currentItems = currentItems.filterNot(wi => wi == item)
      }
      updateAsync(delta, items.tail)
    }
  }
}

object Asynchronous {
  private val beginItem = (item: WorkflowItem) => item.begin()
}