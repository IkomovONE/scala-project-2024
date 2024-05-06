package plant

import glob_val.GlobalValues.{ANSI_GREEN, ANSI_RESET}

class HydropowerPlant(plantName: String, var generatedEnergy: Int) extends Plant(plantName, generatedEnergy) {

  var turbineSpeed = 100

  def increaseTurbineSpeed: Unit = {
    turbineSpeed = turbineSpeed + 10
    println(ANSI_GREEN + "Increase turbine speed on 10%." + ANSI_RESET)
  }

  def reduceTurbineSpeed: Unit = {
    turbineSpeed = turbineSpeed - 10
    println(ANSI_GREEN + "Reduce turbine speed on 10%." + ANSI_RESET)
  }

  override def isGoodQuality: Boolean = {
    if (quality > 10) return true
    false
  }

  override def decrementQuality(): Unit = {
    if (turbineSpeed > 100) {
      quality = quality - 10 - (turbineSpeed - 100)
    } else {
      quality = quality - 10 + (turbineSpeed - 100) / 10
    }
  }

  override def generateEnergy(): Int = {
    generatedEnergy = generatedEnergy * quality / 100 * turbineSpeed / 100
    decrementQuality()
    totalGeneratedEnergy = totalGeneratedEnergy + generatedEnergy
    generatedEnergy
  }
}