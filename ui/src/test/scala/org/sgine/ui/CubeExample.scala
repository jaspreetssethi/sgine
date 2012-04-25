package org.sgine.ui

import com.badlogic.gdx.graphics.Texture
import render.{Vertex, TextureCoordinates}
import org.sgine.{Updatable, Resource}

/**
 * @author Matt Hicks <mhicks@sgine.org>
 */
object CubeExample extends UI with Debug {
  perspective()

  val texture = new Texture(Resource("sgine_256.png"))

  val cube = new ShapeComponent()
  cube._texture := texture
  cube._textureCoordinates := TextureCoordinates.box()
  cube._vertices := Vertex.box(256.0, 256.0, 256.0)
  cube.resolution(1024, 768)
  contents += cube

  updates += new Updatable {
    override def update(delta: Double) = {
      cube.rotation.x += 100.0 * delta
      cube.rotation.y += 75.0 * delta
      cube.rotation.z += 50.0 * delta
    }
  }
}
