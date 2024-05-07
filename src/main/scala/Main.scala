import file_controller.FileController
import menu.Menus.menu
import plant.{HydropowerPlant, SolarpowerPlant, WindpowerPlant}
import system.EnergyPowerSystem


//  Aleksandr Ivanov
//  Daniil Komov
//  Vera Stojcheva
object Main {

  def main(args: Array[String]): Unit = {

    // PLANTS WITH SAME NAMES ARE FORBIDDEN

    val fileController = new FileController("data.csv")

    var hydropowerSystem = new EnergyPowerSystem
    var solarpowerSystem = new EnergyPowerSystem
    var windpowerSystem = new EnergyPowerSystem

    // loading data from the file
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


    val windpowerPlant1 = new WindpowerPlant("WPP-1", 100)
    val windpowerPlant2 = new WindpowerPlant("WPP-2", 200)
    val windpowerPlant3 = new WindpowerPlant("WPP-3", 300)

    val solarpowerPlant1 = new SolarpowerPlant("SPP-1", 100)
    val solarpowerPlant2 = new SolarpowerPlant("SPP-2", 200)
    val solarpowerPlant3 = new SolarpowerPlant("SPP-3", 300)

    hydropowerSystem.addPlant(hydropowerPlant1)
    hydropowerSystem.addPlant(hydropowerPlant2)
    hydropowerSystem.addPlant(hydropowerPlant3)

    windpowerSystem.addPlant(windpowerPlant1)
    windpowerSystem.addPlant(windpowerPlant2)
    windpowerSystem.addPlant(windpowerPlant3)

    solarpowerSystem.addPlant(solarpowerPlant1)
    solarpowerSystem.addPlant(solarpowerPlant2)
    solarpowerSystem.addPlant(solarpowerPlant3)

    menu(fileController, hydropowerSystem,solarpowerSystem,windpowerSystem)
  }
}