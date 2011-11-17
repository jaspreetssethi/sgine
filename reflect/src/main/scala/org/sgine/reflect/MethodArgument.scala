package org.sgine.reflect

import doc.Documentation

/**
 * MethodArgument represents an argument required to invoke a method.
 *
 * @author Matt Hicks <mhicks@sgine.org>
 */
class MethodArgument(val index: Int,
                     val name: String,
                     val `type` : EnhancedClass,
                     defaultMethod: Option[EnhancedMethod],
                     val doc: Option[Documentation]) {
  /**
   * The default value for this argument if one exists.
   */
  def default[T](instance: AnyRef) = defaultMethod.map(m => m(instance).asInstanceOf[T])

  /**
   * True if there is a default value associated with this argument.
   */
  def hasDefault = defaultMethod != None

  override def toString = name + ": " + `type`
}