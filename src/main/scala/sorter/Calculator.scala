package sorter

import event.Event

object Calculator {
  def calculateMeanEnergy(events: Array[Event]): Double = {
    if (events.isEmpty) return 0.0
    val energies = events.map(_.energy)
    energies.sum.toDouble / energies.length
  }

  def calculateMedianEnergy(events: Array[Event]): Double = {
    if (events.isEmpty) return 0.0
    val energies = events.map(_.energy).sorted
    val n = energies.length
    if (n % 2 == 0) (energies(n / 2 - 1) + energies(n / 2)) / 2.0
    else energies(n / 2)
  }

  def calculateModeEnergy(events: Array[Event]): Int = {
    if (events.isEmpty) return 0
    val energyCounts = events.map(_.energy).groupBy(identity).mapValues(_.length)
    val maxFrequency = energyCounts.values.max
    energyCounts.filter(_._2 == maxFrequency).keys.min
  }

  def calculateRangeEnergy(events: Array[Event]): (Int, Int) = {
    if (events.isEmpty) return (0, 0)
    val energies = events.map(_.energy)
    (energies.min, energies.max)
  }

  def calculateMidrangeEnergy(events: Array[Event]): Double = {
    if (events.isEmpty) return 0.0
    val (minEnergy, maxEnergy) = calculateRangeEnergy(events)
    (minEnergy + maxEnergy) / 2.0
  }
}