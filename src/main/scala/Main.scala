object Main {

  val ANSI_RESET = "\u001B[0m"
  val ANSI_GREEN = "\u001B[32m"

  class Plant(val plantName: String, generatedEnergy: Double) {
    var quality = 100

    def isGoodQuality: Boolean = true

    def decrementQuality(): Unit = {
      quality -= 1
    }

    def generateEnergy(): Double = {
      decrementQuality()
      generatedEnergy
    }
  }

  class HydropowerPlant(plantName: String,var generatedEnergy: Double) extends Plant(plantName, generatedEnergy) {

    var turbineSpeed = 100

    def increaseTurbineSpeed: Unit = {
      turbineSpeed = turbineSpeed + 10
      println(ANSI_GREEN+"Increase turbine speed on 10%."+ANSI_RESET)
    }

    def reduceTurbineSpeed: Unit = {
      turbineSpeed = turbineSpeed - 10
      println(ANSI_GREEN+"Reduce turbine speed on 10%."+ANSI_RESET)
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

    override def generateEnergy(): Double = {
      generatedEnergy = generatedEnergy * quality / 100 * turbineSpeed / 100
      decrementQuality()
      generatedEnergy
    }
  }

  // fix error with non int input
  def hydropowerControlMenu(plants: Array[HydropowerPlant]): Unit = {
    println("Choose Hydropower plant:")
    for ((plant, index) <- plants.zipWithIndex) {
      println(s"${index + 1}. ${plant.plantName}")
    }
    print("Plant number: ")
    val userPlantInput = scala.io.StdIn.readInt()
    var generatedEnergy: Double = 0
    while (userPlantInput >= 1 && userPlantInput <= plants.length) {
      val selectedPlant = plants(userPlantInput - 1)

      println("\nChoose "+selectedPlant.plantName+" command option:\n" +
        "1) Check quality.\n" +
        "2) Generate Energy.\n" +
        "3) Increase turbine speed.\n" +
        "4) Reduce turbine speed.\n" +
        "5) Disconnect plant.\n" +
        "Enter any other number to exit.")
      print("Command: ")
      val userChoiceInput = scala.io.StdIn.readInt()
      println("")
      userChoiceInput match {
        case 1 =>{
          println(ANSI_GREEN+"Quality of plant: "+selectedPlant.plantName+" is " + selectedPlant.quality+". "+ANSI_RESET)
        }
        case 2 =>{
          generatedEnergy = generatedEnergy + selectedPlant.generateEnergy()
          println(ANSI_GREEN+"Generated energy of plant: "+selectedPlant.plantName+" is " + selectedPlant.generatedEnergy+". "+ANSI_RESET)
        }
        case 3 =>{
          selectedPlant.increaseTurbineSpeed;
          println(ANSI_GREEN+"Increased turbine speed of plant: "+selectedPlant.plantName+". "+ANSI_RESET)
        }
        case 4 =>{
          selectedPlant.reduceTurbineSpeed;
          println(ANSI_GREEN+"Reduce turbine speed of plant: "+selectedPlant.plantName+". "+ANSI_RESET)
        }
        case 5 =>{
          //
          println(ANSI_GREEN+"Disconnect plant: "+selectedPlant.plantName+" out of the system. "+ANSI_RESET)
        }

        case _ =>{
          //
          hydropowerControlMenu(plants)
        }
      }
      println("")

    }
  }

  def main(args: Array[String]): Unit = {

    val hydropowerPlant1 = new HydropowerPlant("HPP-1", 100)
    val hydropowerPlant2 = new HydropowerPlant("HPP-2", 200)
    val hydropowerPlant3 = new HydropowerPlant("HPP-3", 300)

    hydropowerControlMenu(Array(hydropowerPlant1, hydropowerPlant2, hydropowerPlant3))
  }
}