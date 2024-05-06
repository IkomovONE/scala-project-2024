package event

import java.time.LocalDateTime

class Event (plantName:String,val type_ : String,val date:LocalDateTime,val energy:Int,val capacity:Int, val quality:Int ){

  override def toString: String = s"$plantName, $type_, $date, $energy, $capacity, $quality)"

}
