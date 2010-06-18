package org.sgine.ui

import org.sgine.core.Resource

import org.sgine.render.StandardDisplay

/**
 * Simple display of a ui.Image component.
 * 
 * @author Matt Hicks <mhicks@sgine.org>
 */
object TestImage extends StandardDisplay {
	def setup() = {
		val component = new Image(Resource("puppies.jpg"))
		scene += component
	}
}