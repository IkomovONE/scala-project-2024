package system

import plant.HydropowerPlant

import scala.collection.mutable.ArrayBuffer

class HydropowerSystem {
  val hydropowerPlants = ArrayBuffer.empty[HydropowerPlant]

  def addPlant(plant: HydropowerPlant): Unit = {
    hydropowerPlants += plant
  }

  def disconnect(plant: HydropowerPlant): Unit = {
    val index = hydropowerPlants.indexOf(plant)
    if (index != -1) {
      hydropowerPlants.remove(index)
    }
  }
}