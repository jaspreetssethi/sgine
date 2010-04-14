package org.sgine.ui.ext

import org.sgine.property.container.PropertyContainer

import org.sgine.ui.Component

class Location(override val parent: Component) extends PropertyContainer {
	val x = new HorizontalNumericProperty(0.0, this)
	val y = new VerticalNumericProperty(0.0, this)
	val z = new DepthNumericProperty(0.0, this)
}