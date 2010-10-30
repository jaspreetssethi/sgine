package org.sgine.input

import org.sgine.event.Event
import org.sgine.event.Listenable

import org.sgine.input.event.MouseEvent

import org.sgine.render.Renderer

import org.sgine.work.Updatable

import org.lwjgl.input.{Mouse => GLMouse}

object Mouse extends Listenable {
	val Move = 0
	val Press = 1
	val Release = -1
	val Over = 2
	val Out = -2
	val Click = 4
	val Wheel = 8
	
	def validate() = {
		if (!GLMouse.isCreated) {
			GLMouse.create()
		}
	}
	
	def update(renderer: Renderer) = {
		if (GLMouse.isCreated) {
			while (GLMouse.next()) {
				val button = GLMouse.getEventButton
				val state = GLMouse.getEventButtonState
				val wheel = GLMouse.getEventDWheel
				val x = GLMouse.getEventX
				val y = GLMouse.getEventY
				val dx = GLMouse.getEventDX
				val dy = GLMouse.getEventDY
				
				val windowX = x / (renderer.canvas.getWidth / 2.0) - 1.0
				val windowY = y / (renderer.canvas.getHeight / 2.0) - 1.0
				val windowDX = dx / (renderer.canvas.getWidth / 2.0)
				val windowDY = dy / (renderer.canvas.getHeight / 2.0)

				val nearHeight = 1.0
				val normalizedX = nearHeight * windowX
				val normalizedY = nearHeight * windowY
				val normalizedDX = nearHeight * windowDX
				val normalizedDY = nearHeight * windowDY
				
				val evt = MouseEvent(button, state, wheel, normalizedX, normalizedY, normalizedDX, normalizedDY)
				Event.enqueue(evt)
			}
		}
	}
}