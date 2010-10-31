package org.sgine.ui.layout

import org.sgine.bounding.BoundingObject
import org.sgine.bounding.mutable.BoundingBox

import org.sgine.core.Direction
import org.sgine.core.HorizontalAlignment
import org.sgine.core.VerticalAlignment

import org.sgine.event.Event

import org.sgine.ui.Component
import org.sgine.ui.AbstractContainer

import scala.math._

class BoxLayout private(val direction: Direction, val spacing: Double, val reverse: Boolean = false) extends Layout {
	def apply(container: AbstractContainer) = {
		// Determine size for offset
		var items = 0
		var width = 0.0
		var height = 0.0
		var depth = 0.0
		for (n <- container.children) n match {
			case c: Component => {
				items += 1
				if (direction == Direction.Vertical) {
					width = max(width, c.bounding().width)
					height += c.bounding().height
				} else {
					width += c.bounding().width
					height = max(height, c.bounding().height)
				}
				depth = max(depth, c.bounding().depth)
			}
			case _ =>
		}
		
		// Add spacing
		if (direction == Direction.Vertical) {
			height += spacing * (items - 1)
		} else {
			width += spacing * (items - 1)
		}
		
		// Update location of children
		var position = if (direction == Direction.Vertical) {
			height / 2.0
		} else {
			width / -2.0
		}
		if (reverse) {
			position = -position
		}
		for (n <- container.children) n match {
			case c: Component => {
				if (direction == Direction.Vertical) {
					c.location.x.align := HorizontalAlignment.Center
					c.location.x := 0.0
					c.location.y.align := (if (reverse) VerticalAlignment.Bottom else VerticalAlignment.Top)
					c.location.y := position
					
					if (reverse) {
						position += c.bounding().height
						position += spacing
					} else {
						position -= c.bounding().height
						position -= spacing
					}
				} else {
					c.location.x.align := (if (reverse) HorizontalAlignment.Right else HorizontalAlignment.Left)
					c.location.x := position
					c.location.y.align := VerticalAlignment.Middle
					c.location.y := 0.0
					
					if (reverse) {
						position -= c.bounding().width
						position -= spacing
					} else {
						position += c.bounding().width
						position += spacing
					}
				}
			}
			case _ =>
		}
		
		// Update for padding
		width += container.padding.left() + container.padding.right()
		height += container.padding.top() + container.padding.bottom()
		
		// Set the size on the container
		container.size.measured(width, height, depth)
	}
}

object BoxLayout {
	def apply(direction: Direction = Direction.Vertical, spacing: Double = 0.0, reverse: Boolean = false) = new BoxLayout(direction, spacing, reverse)
}