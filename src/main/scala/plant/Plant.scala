package plant

class Plant(val plantName: String, generatedEnergy: Int) { // abstract class for all plants
  var quality = 100
  var totalGeneratedEnergy: Int = 0

  def isGoodQuality: Boolean = true

  def decrementQuality(): Unit = {
    quality -= 1
  }

  def generateEnergy(): Int = {
    decrementQuality()
    totalGeneratedEnergy = totalGeneratedEnergy + generatedEnergy
    generatedEnergy
  }
}