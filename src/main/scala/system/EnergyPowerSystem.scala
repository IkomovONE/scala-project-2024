package system

import plant.{HydropowerPlant, Plant}

import scala.collection.mutable.ArrayBuffer

class EnergyPowerSystem {
  val plants = ArrayBuffer.empty[Plant]

  def addPlant(plant: Plant): Unit = {
    plants += plant
  }

  def disconnect(plant: Plant): Unit = {
    val index = plants.indexOf(plant)
    if (index != -1) {
      plants.remove(index)
    }
  }
}
