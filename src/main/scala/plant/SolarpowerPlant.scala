package plant

import glob_val.GlobalValues.{ANSI_GREEN, ANSI_RESET}

class SolarpowerPlant(plantName: String, var generatedEnergy: Int) extends Plant(plantName, generatedEnergy) {
    var panelAngle = 0

    def increasePanelAngle (angle: Int): Unit = {
    panelAngle= (panelAngle + angle) % 360

    println(s"${ANSI_GREEN}Incrased panel angle to $panelAngle degrees.${ANSI_RESET}")
    }

    def decreasePanelAngle(angle: Int): Unit= {
        panelAngle= (panelAngle - angle + 360) % 360
        println(s"${ANSI_GREEN}Decreased panel angle to $panelAngle degrees.${ANSI_RESET}")
    }

    override def isGoodQuality: Boolean = {
    if (quality > 20) true else false
    }

  override def decrementQuality(): Unit = {
    val panelAgeDegradation = (90 - panelAngle).abs / 90.0 * 10
    quality = math.max(quality - 10 - panelAgeDegradation.toInt, 0)
  }

  
  override def generateEnergy(): Int = {
    generatedEnergy = generatedEnergy * quality / 100 * ((90 - panelAngle).abs / 90) / 100
    decrementQuality()
    totalGeneratedEnergy = totalGeneratedEnergy + generatedEnergy
    generatedEnergy
  }
} 