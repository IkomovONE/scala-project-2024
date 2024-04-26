object Main {

  class Plant(val plantName: String,val generatedEnergy: Double) {
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

  class HydropowerPlant(plantName: String, generatedEnergy: Double) extends Plant(plantName, generatedEnergy) {

    var turbineSpeed = 100

    def increaseTurbineSpeed(speed: Double): String = {
      turbineSpeed = turbineSpeed + 10
      s"increase turbine speed on 10%"
    }

    def reduceTurbineSpeed(speed: Double): String = {
      turbineSpeed = turbineSpeed - 10
      s"reduce turbine speed on 10%"
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
      decrementQuality()
      generatedEnergy*quality/100*turbineSpeed/100
    }
  }

  def hydropowerControlMenu(plants: Array[HydropowerPlant]): Unit = {
    println("Choose Hydropower plant:")
    for ((plant, index) <- plants.zipWithIndex) {
      println(s"${index + 1}. ${plant.plantName}")
    }
    val userPlantInput = scala.io.StdIn.readInt()
    if (userPlantInput >= 1 && userPlantInput <= plants.length) {
      val selectedPlant = plants(userPlantInput - 1)

      println("\n")
      println("Choose option :\n" +
        "1) Check quality.\n" +
        "2) Generate Energy.\n" +
        "3) Increase turbine speed.\n" +
        "4) Reduce turbine speed.\n" +
        "5) Disconnect plant.\n" +
        "Enter any other button to exit.\n")
      println("Command:")
      val userChoiceInput = scala.io.StdIn.readInt()
      userChoiceInput match {
        case 1 =>{
          println("Quality of plant: "+selectedPlant.plantName+" is " + selectedPlant.quality+". ")
        }
        case 2 =>{
          selectedPlant.generateEnergy()
          println("Generated quality of plant: "+selectedPlant.plantName+" is " + selectedPlant.generatedEnergy+". ")
        }
        case 3 =>{}
        case 4 =>{}
        case 5 =>{}
        case 6 =>{}
        case 7 =>{}
      }

    } else {
      println("Invalid selection.")
    }
  }

  def main(args: Array[String]): Unit = {

    val hydropowerPlant1 = new HydropowerPlant("HPP-1", 100)
    val hydropowerPlant2 = new HydropowerPlant("HPP-2", 200)
    val hydropowerPlant3 = new HydropowerPlant("HPP-3", 300)

  }
}