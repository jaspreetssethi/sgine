package org.sgine.opengl.scene

import org.sgine.event._
import org.sgine.math.mutable._
import org.sgine.opengl.shape._
import org.sgine.opengl.scene.event._

import java.net._
import javax.imageio._
import java.awt.image._;

import org.lwjgl.opengl.GL11._;

class GLImage extends GLShape {
	shape := Shape(GL_QUADS, Vector3.Zero, Vector3.Zero, Vector3.Zero, Vector3.Zero)
	shape().textureCoordinates(0) = Vector2.UnitY;
	shape().textureCoordinates(1) = Vector2.Ones;
	shape().textureCoordinates(2) = Vector2.UnitX;
	shape().textureCoordinates(3) = Vector2.Zero;
	
	def load(url: URL, width: Int = -1, height: Int = -1) = {
		Event.enqueue(new ImageLoadingEvent(this))
		val f = () => {
			apply(ImageIO.read(url), width, height)
			waitForRender()
			Event.enqueue(new ImageLoadedEvent(this))
		}
		Event.workManager += f
	}
	
	def apply(image: BufferedImage, width: Int = -1, height: Int = -1) = {
		shape()(image, width, height)
	}
}