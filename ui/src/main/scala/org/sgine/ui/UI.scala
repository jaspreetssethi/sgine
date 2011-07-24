/*
 * Copyright (c) 2011 Sgine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *  Neither the name of 'Sgine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sgine.ui

import org.sgine.property.{MutableProperty, Property, ImmutableProperty}
import org.sgine.render.{RenderApplication, Renderer}
import org.sgine.scene.ContainerView
import org.sgine.Updatable
import org.sgine.math.Matrix4
import org.sgine.input.Mouse
import org.sgine.event.EventHandler
import org.sgine.input.event.MouseEvent

/**
 * 
 *
 * @author Matt Hicks <mhicks@sgine.org>
 */
class UI extends Container {
  val updatableView = new ImmutableProperty(new ContainerView[Updatable](this))
  val rendererView = new ImmutableProperty(new ContainerView[RenderableComponent](this))
  val boundingView = new ImmutableProperty(new ContainerView[MatrixComponent](this))    // TODO: switch to BoundingComponent
  val renderer: Property[Renderer] = new MutableProperty[Renderer]()

  /**
   * The window bounds for drawing the content within in pixels.
   *
   * Defaults to 1024 -> 768
   */
  def windowSize = 1024 -> 768

  /**
   * The translated screen size for pixel mapping.
   *
   * Defaults to 1024.0 -> 768.0
   */
  def screenSize = 1024.0 -> 768.0

  val camera = new Camera()

  override protected def updateMatrix(matrix: Matrix4) = {
    super.updateMatrix(matrix)
    matrix.concatenate(camera.view)
  }

  resolution(screenSize._1, screenSize._2)

  Mouse.mouseEvent += EventHandler()(processMouseEvent _)

  private def processMouseEvent(evt: MouseEvent) = {
    boundingView().foreach(pickTest)
  }

  private val pickTest = (mc: MatrixComponent) => {
//    val origin = 
  }

  private object renderApplication extends RenderApplication {
    private val updateComponent = (u: Updatable) => u.update()
    private val renderComponent = (c: RenderableComponent) => RenderableComponent.render(c)

    def update() = {
      UI.this.update()
      updatableView.foreach(updateComponent)
    }

    def render() = rendererView.foreach(renderComponent)

    def dispose() = {}

    override def title = {
      val className = UI.this.getClass.getSimpleName
      className.substring(0, className.length - 1)
    }

    override def screenSize = windowSize
  }

  final def main(args: Array[String]): Unit = {
    renderer.asInstanceOf[MutableProperty[Renderer]](Renderer(renderApplication, windowSize._1, windowSize._2))
  }
}