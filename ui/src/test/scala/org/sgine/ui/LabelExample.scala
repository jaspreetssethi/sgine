package org.sgine.ui

import font.BitmapFont
import org.sgine.Resource

/**
 *
 *
 * @author Matt Hicks <mhicks@sgine.org>
 */
object LabelExample extends UI {
  implicit val font = BitmapFont(Resource("arial64.fnt"))
  contents += Label("Hello World!")
}