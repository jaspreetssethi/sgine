package org.sgine.property;

import scala.reflect.Manifest

/**
 * MutableProperty provides an extremely simple Property implementation
 * that may be modified. Calls to <code>apply(t:T)</code> will modify the
 * current instance and return a reference back to itself.
 * 
 * @author Matt Hicks
 */
class MutableProperty[T](protected implicit val manifest: Manifest[T]) extends Property[T] {
	def this(initialValue: T)(implicit manifest: Manifest[T]) = {
		this()
		apply(initialValue)
	}

	@volatile protected var value: T = _

	def apply() = value
	
	def apply(value: T): Property[T] = {
		this.value = value
		
		this
	}
}