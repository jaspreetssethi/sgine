package org.sgine.render.shape

import java.util.concurrent.atomic.AtomicReference

import org.sgine.core._

import org.sgine.render.shape.renderer._
import org.sgine.render.shape.renderer.lwjgl._

class MutableShape private() extends Shape {
	var cull: Face = Face.None
	var material: Material = Material.AmbientAndDiffuse
	var mode: ShapeMode = ShapeMode.Triangles
	
	def vertex = _dataVertex.get
	def color = _dataColor.get
	def texture = _dataTexture.get
	def normal = _dataNormal.get
	
	def vertex_=(data: VertexData) = apply(data)
	def color_=(data: ColorData) = apply(data)
	def texture_=(data: TextureData) = apply(data)
	def normal_=(data: NormalData) = apply(data)
	
	private val _dataVertex = new AtomicReference[VertexData]
	private val _dataColor = new AtomicReference[ColorData]
	private val _dataTexture = new AtomicReference[TextureData]
	private val _dataNormal = new AtomicReference[NormalData]
	
	private val _updateVertex = new AtomicReference[VertexData]
	private val _updateColor = new AtomicReference[ColorData]
	private val _updateTexture = new AtomicReference[TextureData]
	private val _updateNormal = new AtomicReference[NormalData]

	def dirty = _updateVertex.get != null ||
				_updateColor.get != null ||
				_updateTexture.get != null ||
				_updateNormal.get != null
	
	override def apply() = {
		update()
		
		super.apply()
	}
	
	def update() = {
		if (dirty) {
			// Updates
			val dataVertex = _updateVertex.getAndSet(null)
			val dataColor = _updateColor.getAndSet(null)
			val dataTexture = _updateTexture.getAndSet(null)
			val dataNormal = _updateNormal.getAndSet(null)
			
			val vc = dataVertex match {
				case null => false
				case d => {
					_dataVertex.set(d)
					true
				}
			}
			
			val cc = dataColor match {
				case null => false
				case d => {
					_dataColor.set(d)
					true
				}
			}
			
			val tc = dataTexture match {
				case null => false
				case d => {
					_dataTexture.set(d)
					true
				}
			}
			
			val nc = dataNormal match {
				case null => false
				case d => {
					_dataNormal.set(d)
					true
				}
			}
			
			renderer.update(this, vc, cc, tc, nc)
		}
	}
	
	def apply(data: VertexData) = _updateVertex.set(data)
	
	def apply(data: ColorData) = _updateColor.set(data)
	
	def apply(data: TextureData) = _updateTexture.set(data)
	
	def apply(data: NormalData) = _updateNormal.set(data)
}

object MutableShape {
	def apply() = new MutableShape()
}