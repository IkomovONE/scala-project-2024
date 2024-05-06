import file_controller.FileController
import menu.Menus.menu
import plant.HydropowerPlant
import system.{EnergyPowerSystem}

object Main {

  def main(args: Array[String]): Unit = {

    val fileController = new FileController("data.csv")

    var hydropowerSystem = new EnergyPowerSystem
    var solarpowerSystem = new EnergyPowerSystem
    var windpowerSystem = new EnergyPowerSystem

    val allSystems:Array[EnergyPowerSystem] = fileController.loadData()

    if(allSystems.headOption.exists(_.plants.nonEmpty)){
      //solar
      solarpowerSystem = allSystems.headOption.orNull
    }
    if(allSystems.lift(1).exists(_.plants.nonEmpty)){
      //wind
      windpowerSystem = allSystems.lift(1).orNull
    }
    if(allSystems.lift(2).exists(_.plants.nonEmpty)){
      //hydro
      hydropowerSystem = allSystems.lift(2).orNull

    }


    val hydropowerPlant1 = new HydropowerPlant("HPP-1", 100)
    val hydropowerPlant2 = new HydropowerPlant("HPP-2", 200)
    val hydropowerPlant3 = new HydropowerPlant("HPP-3", 300)

    hydropowerSystem.addPlant(hydropowerPlant1)
    hydropowerSystem.addPlant(hydropowerPlant2)
    hydropowerSystem.addPlant(hydropowerPlant3)

    // loading data from the file



    menu(fileController, hydropowerSystem,solarpowerSystem,windpowerSystem)
  }
}