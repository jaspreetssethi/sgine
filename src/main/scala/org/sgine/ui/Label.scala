package org.sgine.ui

import org.sgine.bounding.BoundingObject
import org.sgine.bounding.mutable.BoundingQuad

import org.sgine.core.ProcessingMode

import org.sgine.event.Event
import org.sgine.event.EventHandler

import org.sgine.property.AdvancedProperty
import org.sgine.property.event.PropertyChangeEvent

import org.sgine.render.font.Font
import org.sgine.render.font.FontManager

class Label extends Component {
	val font = new AdvancedProperty[Font](FontManager("Arial", 24), this)
	val text = new AdvancedProperty[String]("", this)
	
	def this(text: String) = {
		this()
		
		this.text := text
	}
	
	configureListeners()
	
	def drawComponent() = {
		if ((font() != null) && (text() != null)) {
			font().drawString(text())
		}
	}
	
	private def configureListeners() = {
		font.listeners += EventHandler(updateBounding, ProcessingMode.Blocking)
		text.listeners += EventHandler(updateBounding, ProcessingMode.Blocking)
	}
	
	private def updateBounding(evt: PropertyChangeEvent[AnyRef]) = {
		var width = 0.0
		var height = 0.0
		if ((font() != null) && (text() != null)) {
			width = font().measureWidth(text())
			height = font().lineHeight
		}
		
		if ((width != size.actual.width()) || (height != size.actual.height())) {
			size.actual.width := width
			size.actual.height := height
		}
	}
	
	override def toString() = "Label(text = " + text + ")"
}