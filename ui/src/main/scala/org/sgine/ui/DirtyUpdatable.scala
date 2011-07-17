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

import java.util.concurrent.atomic.AtomicBoolean
import org.sgine.event.{EventHandler, Listenable}
import org.sgine.Updatable

/**
 * 
 *
 * @author Matt Hicks <mhicks@sgine.org>
 */
trait DirtyUpdatable extends Updatable {
  private var dirtyFlags: List[DirtyFlag] = Nil

    /**
   * Allows flagging of specific events <code>E</code> on <code>listenables</code> to fire the function <code>f</code>
   * in the <code>update</code> method.  If the function <code>f</code> returns false, the flag will be reset and
   * attempted again in the next update. This method returns the DirtyFlag instance created.
   */
  protected def dirtyUpdate[E](name: String, listenables: Listenable*)(f: => Any)(implicit manifest: Manifest[E]) = {
    val dirtyFlag = new DirtyFlag(name)(f)
    val handler = EventHandler[E]() {
      case evt => dirtyFlag.flag.set(true)
    }
    listenables.foreach(l => l.listeners += handler)
    synchronized {
      dirtyFlags = dirtyFlag :: dirtyFlags
    }
    dirtyFlag
  }

  def update() = {
    dirtyFlags.foreach(DirtyFlag.invokeDirtyFlag)
  }
}

class DirtyFlag(val name: String)(f: => Any) {
  val flag = new AtomicBoolean(false)

  def invoke() = f
}

object DirtyFlag {
  private[ui] val invokeDirtyFlag = (dirtyFlag: DirtyFlag) => {
    if (dirtyFlag.flag.get()) {
      dirtyFlag.flag.set(false)
      if (dirtyFlag.invoke() == false) {
        dirtyFlag.flag.set(true)    // Reset flag if false is returned by function
      }
    }
  }
}