package org.sgine.ui.layout

import org.sgine.bounding.BoundingObject
import org.sgine.bounding.event.BoundingChangeEvent
import org.sgine.bounding.mutable.BoundingBox

import org.sgine.core.HorizontalAlignment
import org.sgine.core.VerticalAlignment

import org.sgine.event.Event

import org.sgine.log._

import org.sgine.scene.Node
import org.sgine.scene.NodeContainer

import org.sgine.ui.ext.LocationComponent

class GridLayout private(val rows: Int, val columns: Int, val spacing: Int, val itemWidth: Double, val itemHeight: Double) extends Layout {
	val width = (columns * itemWidth) + ((columns - 1) * spacing)
	val height = (rows * itemHeight) + ((rows - 1) * spacing)
	
	private var items: List[GridItem] = Nil
	
	def apply(container: NodeContainer) = {
		// Make sure everything is configured
		if (container.size != items.size) {
			// Add missing
			for (n <- container) {
				if (get(n) == None) {
					nextAvailable match {
						case Some((row, column)) => apply(n, row, column)
						case None => warn("No more room found in GridLayout")
					}
				}
			}
			// Remove no longer used
			for (item <- items) {
				if (container.indexOf(item.n) == -1) {
					items = items filterNot (i => item == i)
				}
			}
		}
		
		// Lay out each item
		for (item <- items) {
			layout(item)
		}
		
		// Update bounding of container if necessary
		container match {
			case bo: BoundingObject => bo.bounding() match {
				case bb: BoundingBox => {
					bb.width = width
					bb.height = height
					
					val e = new BoundingChangeEvent(bo, bb)
					Event.enqueue(e)
				}
				case _ =>
			}
			case _ =>
		}
	}
	
	def apply(n: Node, row: Int, column: Int) = {
		val item = get(n) match {
			case Some(i) => i
			case None => {
				val i = GridItem(n, row, column)
				synchronized {
					items = i :: items
				}
				i
			}
		}
		item.row = row
		item.column = column
		
		layout(item)
	}
	
	def nextAvailable = (0 until rows * columns) find(value => {
		val (row, column) = rc(value)
		!isUsed(row, column)
	}) match {
		case Some(value) => Some(rc(value))
		case None => None
	}
	
	private def rc(value: Int) = (value / columns, value % columns)
	
	def isUsed(row: Int, column: Int) = items.find(item => item.row == row && item.column == column) != None
	
	private def layout(item: GridItem) = {
		val offsetX = (item.column * (itemWidth + spacing)) - (width / 2.0)
		val offsetY = (-item.row * (itemHeight + spacing)) - (height / -2.0)
		item.n match {
			case c: LocationComponent => {
				c.location.x.align := HorizontalAlignment.Left
				c.location.y.align := VerticalAlignment.Top
				c.location.set(offsetX, offsetY)
			}
		}
	}
	
	private def get(n: Node) = items.find(item => item.n == n)
}

private case class GridItem(n: Node, var row: Int, var column: Int)

object GridLayout {
	def apply(rows: Int, columns: Int, spacing: Int, itemWidth: Double, itemHeight: Double) = {
		new GridLayout(rows, columns, spacing, itemWidth, itemHeight)
	}
}