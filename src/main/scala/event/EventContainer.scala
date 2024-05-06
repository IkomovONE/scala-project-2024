package event

import scala.collection.mutable.ArrayBuffer

object EventContainer {
  var events = ArrayBuffer.empty[Event]

  def addEvent(event: Event): Unit = {
    events += event
  }
  def reset():Unit = {
    events = ArrayBuffer.empty[Event]
  }
}
