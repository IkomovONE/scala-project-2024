package sorter

object Calculator {
  def calculateMeanEnergy(energies: Array[Int]): Double = {
    if (energies.isEmpty) return 0.0
    energies.sum.toDouble / energies.length
  }

  def calculateMedianEnergy(energies: Array[Int]): Double = {
    if (energies.isEmpty) return 0.0
    val sortedEnergies = energies.sorted
    val n = sortedEnergies.length
    if (n % 2 == 0) (sortedEnergies(n / 2 - 1) + sortedEnergies(n / 2)) / 2.0
    else sortedEnergies(n / 2)
  }

  def calculateModeEnergy(energies: Array[Int]): Int = {
    if (energies.isEmpty) return 0
    val frequencyMap = energies.groupBy(identity).mapValues(_.length)
    val maxFrequency = frequencyMap.values.max
    frequencyMap.filter(_._2 == maxFrequency).keys.min
  }

  def calculateRangeEnergy(energies: Array[Int]): (Int, Int) = {
    if (energies.isEmpty) return (0, 0)
    (energies.min, energies.max)
  }

  def calculateMidrangeEnergy(energies: Array[Int]): Double = {
    if (energies.isEmpty) return 0.0
    val (minEnergy, maxEnergy) = calculateRangeEnergy(energies)
    (minEnergy + maxEnergy) / 2.0
  }
}