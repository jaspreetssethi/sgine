package org.sgine.event

import org.sgine.ref.ReferenceType
import org.sgine.{Priority, ProcessingMode}
import org.sgine.concurrent.{Time, Executor}

/**
 * @author Matt Hicks <mhicks@sgine.org>
 */
case class EventListenerBuilder(private val listenable: Listenable,
                                private val _filter: Event => Boolean = null,
                                private val processingMode: ProcessingMode = ProcessingMode.Asynchronous,
                                private val maxInvocation: Int = Int.MaxValue,
                                private val referenceType: ReferenceType = ReferenceType.Hard,
                                private val priority: Priority = Priority.Normal) {
  def synchronous = copy(processingMode = ProcessingMode.Synchronous)
  def asynchronous = copy(processingMode = ProcessingMode.Asynchronous)
  def concurrent = copy(processingMode = ProcessingMode.Concurrent)
  def once = copy(maxInvocation = 1)
  def maximum(max: Int) = copy(maxInvocation = max)
  object filter {
    def apply(f: Event => Boolean) = copy(_filter = f)
    def or(f: Event => Boolean) = {
      val oldFilter = _filter
      copy(_filter = {
        case event => oldFilter(event) || f(event)
      })
    }
    def and(f: Event => Boolean) = {
      val oldFilter = _filter
      copy(_filter = {
        case event => oldFilter(event) && f(event)
      })
    }
  }
  def reference(referenceType: ReferenceType) = copy(referenceType = referenceType)
  def priority(priority: Priority) = copy(priority = priority)

  def apply(f: PartialFunction[Event, Any]) = {
    val function = Listener.withFallthrough(f)
    val filter: Event => Boolean = _filter match {
      case null => {
        case event => event.target == listenable
      }
      case existingFilter => existingFilter
    }
    val listener = EventListener(listenable, function, filter, maxInvocation, processingMode, priority)
    listenable.addListener(listener, referenceType)
  }

  def waitFor[T <: Event, R](time: Double,
                             precision: Double = 0.01,
                             start: Long = System.currentTimeMillis(),
                             errorOnTimeout: Boolean = false)
                            (f: PartialFunction[T, R])(implicit manifest: Manifest[T]): Option[R] = {
    var result: Option[R] = None
    var finished = false
    val function = f.lift
    var listener: Listener = null
    listener = apply {
      case event if (manifest.erasure.isAssignableFrom(event.getClass)) => if (!finished) {
        function(event.asInstanceOf[T]) match {
          case None => // Hasn't returned what we want yet
          case s => {
            listenable.removeListener(listener)
            result = s
            finished = true
          }
        }
      }
      case _ => // Not the right type
    }
    Time.waitFor(time, precision, start, errorOnTimeout) {
      result != None
    }
    listenable.removeListener(listener)
    result
  }

  def +=(listener: Listener) = {
    listenable.addListener(listener)
  }

  def -=(listener: Listener) = {
    listenable.removeListener(listener)
  }

  def values = listenable.listenersList
}

case class EventListener(listenable: Listenable,
                         function: Function[Event, Any],
                         filter: Event => Boolean,
                         maxInvocation: Int,
                         processingMode: ProcessingMode,
                         override val priority: Priority) extends Listener {
  private var count = 0

  def apply(event: Event) = {
    count += 1
    val result = processingMode match {
      case ProcessingMode.Synchronous => function(event)
      case ProcessingMode.Asynchronous => listenable.asynchronousActor ! (() => function(event))
      case ProcessingMode.Concurrent => Executor.invoke {
        function(event)
      }
    }
    if (count >= maxInvocation) {
      listenable.removeListener(this)
    }
    result
  }
  def acceptFilter = filter
}