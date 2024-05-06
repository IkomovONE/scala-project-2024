package event

import scala.collection.mutable.ArrayBuffer

object EventContainer {
  val events = ArrayBuffer.empty[Event]

  def addEvent(event: Event): Unit = {
    events += event
  }

}
