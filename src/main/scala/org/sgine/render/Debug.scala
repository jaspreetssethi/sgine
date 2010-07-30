package org.sgine.render

import org.sgine.event.EventHandler
import org.sgine.event.ProcessingMode

import org.sgine.input.Key
import org.sgine.input.Keyboard
import org.sgine.input.event.KeyPressEvent

import org.sgine.render.font.FontManager

import org.sgine.scene.GeneralNodeContainer
import org.sgine.scene.ext.ResolutionNode

import org.sgine.ui.FPSLabel

trait Debug extends Display {
	val debugContainer = new GeneralNodeContainer() with ResolutionNode
	
	abstract override def init() = {
		super.init()
		
		renderer.verticalSync := false
		
		debugContainer.setResolution(1600, 1200)
		scene += debugContainer
		
		val fps = new FPSLabel()
		fps.font := FontManager("lcd")
		fps.location.x := -795.0
		fps.location.x.align := org.sgine.core.HorizontalAlignment.Left
		fps.location.y := 595.0
		fps.location.y.align := org.sgine.core.VerticalAlignment.Top
		fps.location.z := 1.0
		debugContainer += fps
		
		Keyboard.listeners += EventHandler(handleKey, ProcessingMode.Blocking)
	}
	
	private def handleKey(evt: KeyPressEvent) = {
		if (evt.key == Key.Escape) {					// Shutdown
			Renderer().shutdown()
		} else if (evt.keyChar.toLower == 'l') {		// Toggle lighting
			Renderer().lighting := !Renderer().lighting()
			println("Lighting turned " + (if (Renderer().lighting()) "on" else "off"))
		}
	}
}