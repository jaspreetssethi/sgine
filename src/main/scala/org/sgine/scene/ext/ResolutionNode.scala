package org.sgine.scene.ext

import simplex3d.math._
import simplex3d.math.doublem._

trait ResolutionNode extends WorldMatrixNode {
	def setResolution(width: Double, height: Double) = {
		worldMatrix := Mat3x4d.Identity.translate(Vec3d(0.0, 0.0, -101.0)).scale(2.02 / height)
	}
}