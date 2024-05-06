package plant

import glob_val.GlobalValues.{ANSI_GREEN, ANSI_RED, ANSI_RESET}

class WindpowerPlant(plantName: String, var generatedEnergy: Int) extends Plant(plantName, generatedEnergy){

  var turnAngle = 0


  def turnAngleRight: Unit = {

    if (turnAngle == 360) turnAngle = 0

    turnAngle = turnAngle + 10

    
    println(ANSI_GREEN + "Turn the structure right 10 degrees." + ANSI_RESET)
  }


  def turnAngleLeft: Unit = {

    if (turnAngle == 0) turnAngle = 360

    turnAngle = turnAngle - 10
    println(ANSI_GREEN + "Turn the structure right 10 degrees." + ANSI_RESET)
  }


  override def isGoodQuality: Boolean = {
    if (quality > 15) return true
    false
  }


  override def decrementQuality(): Unit = {
    val WindpowerDegr = 10
    quality = math.max(quality - 10 - WindpowerDegr.toInt, 0)
  }


  override def generateEnergy(): Int = {
    generatedEnergy = generatedEnergy * quality / 100000
    decrementQuality()
    totalGeneratedEnergy = totalGeneratedEnergy + generatedEnergy
    if(isGoodQuality){
      print(ANSI_RED+"Please disconnect the plant to avoid low efficiency energy generation and failure.\n"+ANSI_RESET)
    }
    generatedEnergy
  }
}