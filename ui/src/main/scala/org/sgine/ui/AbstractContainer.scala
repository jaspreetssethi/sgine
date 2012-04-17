package org.sgine.ui

import org.sgine.scene.AbstractMutableContainer
import org.sgine.event.ChangeEvent
import org.sgine.property.Property

/**
 * AbstractContainer provides all the functionality for a Component container, but the mutability of its children is
 * protected to the class for better encapsulation when creating complex Components.
 *
 * @author Matt Hicks <mhicks@sgine.org>
 */
class AbstractContainer extends AbstractMutableContainer[Component] with Component {
  listeners.synchronous.filter.descendant() {
    case event: ChangeEvent[_] => event.target match {
      case property: Property[_] if (property.name != null) => println("Changed: " + event.target.getClass + " / " + event)
      case _ => // Ignore
    }
  }

  override protected[ui] def updateMatrix() = {
    super.updateMatrix()

    contents.foreach(AbstractContainer.updateChildMatrix)
  }

  override protected[ui] def updateColor() = {
    super.updateColor()

    contents.foreach(AbstractContainer.updateChildColor)
  }
}

object AbstractContainer {
  private val updateChildMatrix = (child: Component) => child.updateMatrix()
  private val updateChildColor = (child: Component) => child.updateColor()
}