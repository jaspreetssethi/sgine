/*
 * Created by IntelliJ IDEA.
 * User: mhicks
 * Date: 2/19/11
 * Time: 8:01 PM
 */
package org.sgine.opengl.generator

import annotation.tailrec

class GLCreator(combiner: Combiner) {
  private var glBuilder = new StringBuilder()
  private var lwjglBuilder = new StringBuilder()
  private var androidBuilder = new StringBuilder()
  private var b = glBuilder

  private def isAndroid = b eq androidBuilder
  private def isLWJGL = b eq lwjglBuilder

  def gl() = b = glBuilder
  def android() = b = androidBuilder
  def lwjgl() = b = lwjglBuilder

  private var first = true

  private val methods = combiner.methods.flatMap(cm => cm.methods)

  val glString = createGL()
  val androidString = createAndroid()
  val lwjglString = createLWJGL()

  private def createGL() = {
    gl()
    b.clear()

    this % "package org.sgine.opengl"
    nl()
    docStart(0)
    doc("Generated by org.sgine.opengl.generator.Generator", 0)
    doc(tabs = 0)
    doc("Documentation information pulled from <a href=\"http://www.opengl.org/sdk/docs/man/\">http://www.opengl.org/sdk/docs/man/</a>.", 0)
    doc(tabs = 0)
    doc("@see org.sgine.opengl.generator.Generator", 0)
    docEnd(0)
    this % "trait GL {"
    first = true
    methods.foreach(writeMethod(_, true))
    this % "}"
    nl()
    this % "object GL extends GL {"
    tab(1)
    this % "private val local = new ThreadLocal[GL]"
    tab(1)
    this % "def instance = local.get"
    tab(1)
    this % "def instance_=(gl: GL) = local.set(gl)"
    nl()
    first = true
    combiner.fields.foreach(writeField)
    methods.foreach(writeMethodWrapper)
    this + "}"

    b.toString
  }

  private def createAndroid() = {
    android()
    createImplementation()
  }

  private def createLWJGL() = {
    lwjgl()
    createImplementation()
  }

  private def createImplementation() = {
    b.clear()

    this + "package org.sgine.opengl."
    this % (if (isAndroid) "android" else "lwjgl")
    nl()
    docStart(0)
    doc("Generated by org.sgine.opengl.generator.Generator", 0)
    doc(tabs = 0)
    doc("Documentation information pulled from <a href=\"http://www.opengl.org/sdk/docs/man/\">http://www.opengl.org/sdk/docs/man/</a>.", 0)
    doc(tabs = 0)
    doc("@see org.sgine.opengl.generator.Generator", 0)
    docEnd(0)
    this + "class GL"
    if (isAndroid) {
      this + "(instance: javax.microedition.khronos.opengles.GL11)"
    }
    this % " extends org.sgine.opengl.GL {"
    first = true
    methods.foreach(writeImplementationMethod)
    this + "}"

    b.toString
  }

  private def +(s: String) = b.append(s)

  private def %(s: String) = {
    this + s
    nl()
  }

  @tailrec
  private def nl(count: Int = 1): Unit = {
    if (count > 0) {
      this + "\r\n"
      nl(count - 1)
    }
  }

  @tailrec
  private def tab(count: Int = 1): Unit = {
    if (count > 0) {
      this + "\t"
      tab(count - 1)
    }
  }

  private def docStart(tabs: Int = 1) = {
    tab(tabs)
    this % "/**"
  }

  private def doc(s: String = "", tabs: Int = 1) = {
    tab(tabs)
    this + " * "
    this % s
  }

  private def docEnd(tabs: Int = 1) = {
    tab(tabs)
    this % " */"
  }

  private def writeField(field: CombinedField) = {
    if (first) {
      first = false
    } else {
      nl()
    }
    docStart()
    doc("Constant Value: " + field.value.toString)
    doc()
    doc("@see " + field.leftOrigin.getDeclaringClass.getName + "#" + field.name)
    doc("@see " + field.rightOrigin.getDeclaringClass.getName + "#" + field.name)
    docEnd()
    tab()
    this + "val "
    this + field.name
    this + ": "
    this + Generator.convertClass(field.fieldType)
    this + " = "
    this % field.value.toString
  }

  private def writeMethod(method: CombinedMethod, isAbstract: Boolean) = {
    if (first) {
      first = false
    } else {
      nl()
    }
    docStart()
    docEnd()
    tab()
    this + "def "
    this + method.name
    this + "("
    var firstArg = true
    for ((argName, argType) <- method.argNames.zip(method.args)) {
      if (firstArg) {
        firstArg = false
      } else {
        this + ", "
      }
      this + argName
      this + ": "
      this + Generator.convertClass(argType)
    }
    this + "): "
    this + Generator.convertClass(method.returnType)
    if (isAbstract) {
      nl()
    } else {
      this + " = {"
      nl()
    }
  }

  private def writeMethodWrapper(method: CombinedMethod) = {
    writeMethod(method, false)
    tab(2)
    this + "instance."
    this + method.name
    this + "("
    for ((argName, index) <- method.argNames.zipWithIndex) {
      if (index > 0) {
        this + ", "
      }
      this + argName
    }
    this + ")"
    nl()
    tab()
    this + "}"
    nl()
  }

  private def writeImplementationMethod(method: CombinedMethod) = {
    if (first) {
      first = false
    } else {
      nl()
    }
    val signature = if (isAndroid) {
      method.left
    } else {
      method.right
    }
    this + signature.code
    nl()
  }
}