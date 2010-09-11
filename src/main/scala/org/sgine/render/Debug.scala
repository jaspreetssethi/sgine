package org.sgine.render

import org.sgine.core.Resource

import org.sgine.event.EventHandler
import org.sgine.event.ProcessingMode

import org.sgine.input.Key
import org.sgine.input.Keyboard
import org.sgine.input.event.KeyPressEvent

import org.sgine.log._

import org.sgine.render.font.FontManager

import org.sgine.scene.GeneralNodeContainer
import org.sgine.scene.ext.ResolutionNode

import org.sgine.ui.FPSLabel
import org.sgine.ui.Image

trait Debug extends Display {
	val debugContainer = new GeneralNodeContainer() with ResolutionNode
	val fps = new FPSLabel()
	val grid = new Image()
	
	abstract override def init() = {
		super.init()
		
		renderer.verticalSync := false
		
		debugContainer.setResolution(1600, 1200)
		scene += debugContainer
		
		fps.font := FontManager("lcd")
		fps.location.x := -795.0
		fps.location.x.align := org.sgine.core.HorizontalAlignment.Left
		fps.location.y := 595.0
		fps.location.y.align := org.sgine.core.VerticalAlignment.Top
		fps.location.z := 1.0
		debugContainer += fps
		
		grid.source := Resource("grid.png")
		grid.location.z := 525.0
		grid.visible := false
		debugContainer += grid
		
		Keyboard.listeners += EventHandler(handleKey, ProcessingMode.Blocking, worker = renderer)
	}
	
	private def handleKey(evt: KeyPressEvent) = {
		if (evt.key == Key.Escape) {					// Shutdown
			Renderer().shutdown()
		} else if (evt.keyChar.toLower == 'l') {		// Toggle lighting
			Renderer().lighting := !Renderer().lighting()
			info("Lighting turned " + (if (Renderer().lighting()) "on" else "off"))
		} else if (evt.keyChar.toLower == 'f') {		// Toggle fps display
			fps.visible := !fps.visible()
			info("FPS %1s", args = List(if (Renderer().fullscreen()) "enabled" else "disabled"))
		} else if (evt.keyChar.toLower == 'w') {
			if (Renderer().polygonFront() == PolygonMode.Fill) {
				Renderer().polygonFront := PolygonMode.Line
				Renderer().polygonBack := PolygonMode.Line
				info("Switched to polygon line")
			} else {
				Renderer().polygonFront := PolygonMode.Fill
				Renderer().polygonBack := PolygonMode.Fill
				info("Switched to polygon fill")
			}
		} else if (evt.menuDown) {
			if (evt.key == Key.Enter) {
				Renderer().fullscreen := !Renderer().fullscreen()
				info("Grid %1s", args = List(if (Renderer().fullscreen()) "enabled" else "disabled"))
			}
		} else if (evt.keyChar.toLower == 'g') {
			grid.visible := !grid.visible()
			info("Grid %1s", args = List(if (grid.visible()) "enabled" else "disabled"))
		} else if (evt.keyChar.toLower == 'v') {
			Renderer().verticalSync := !Renderer().verticalSync()
			info("Vertical Sync %1s", args = List(if (Renderer().verticalSync()) "enabled" else "disabled"))
		}
	}
}