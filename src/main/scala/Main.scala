import file_controller.FileController
import menu.Menus.menu
import plant.HydropowerPlant
import system.{EnergyPowerSystem}

object Main {

  def main(args: Array[String]): Unit = {

    val fileController = new FileController("data.csv")

    val hydropowerSystem = new EnergyPowerSystem
    val solarpowerPlant = new EnergyPowerSystem
    val windpowerPlant = new EnergyPowerSystem

    val hydropowerPlant1 = new HydropowerPlant("HPP-1", 100)
    val hydropowerPlant2 = new HydropowerPlant("HPP-2", 200)
    val hydropowerPlant3 = new HydropowerPlant("HPP-3", 300)

    hydropowerSystem.addPlant(hydropowerPlant1)
    hydropowerSystem.addPlant(hydropowerPlant2)
    hydropowerSystem.addPlant(hydropowerPlant3)


    menu(fileController, hydropowerSystem,solarpowerPlant,windpowerPlant)
  }
}