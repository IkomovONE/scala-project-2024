package file_controller

import com.github.tototoshi.csv.{CSVReader, CSVWriter}
import glob_val.GlobalValues.{defaultStorageCapacity, storageCapacity}
import plant.HydropowerPlant
import system.EnergyPowerSystem

import java.io.File
import java.time.LocalDate
import scala.util.control.NonFatal

class FileController(filePath: String) { //  class for which works with files
  private val outputFile = new File(filePath)

  // Perform file checks and header writing in the constructor
  try {
    if (!outputFile.exists()) {
      createHeader()
    } else {
      val csvReader = CSVReader.open(outputFile)
      try {
        val rows = csvReader.all()
        if (rows.isEmpty || !rows.head.equals(List("Name", "Type", "Date", "Energy", "Capacity"))) {
          clearAndCreateHeader()
        }
      } finally {
        csvReader.close()
      }
    }
  } catch {
    case NonFatal(e) => println(s"Error creating or writing to file: ${e.getMessage}")
  }

  private def createHeader(): Unit = {
    try {
      val csvWriter = CSVWriter.open(outputFile)
      csvWriter.writeRow(List("Name", "Type", "Date", "Energy", "Capacity"))
      csvWriter.close()
    } catch {
      case NonFatal(e) => println(s"Error writing header row: ${e.getMessage}")
    }
  }

  private def clearAndCreateHeader(): Unit = {
    try {
      outputFile.delete()
      outputFile.createNewFile()
      createHeader()
    } catch {
      case NonFatal(e) => println(s"Error clearing and recreating file: ${e.getMessage}")
    }
  }

  def writedata(name: String, eType: String, date: LocalDate, energy: Int): Unit = {
    val formattedDate = date.toString
    val formattedEnergy = energy.toString
    val formattedCapacity = s"${storageCapacity}/${defaultStorageCapacity}"
    val csvWriter = CSVWriter.open(outputFile, append = true)
    csvWriter.writeRow(List(name, eType, formattedDate, formattedEnergy, formattedCapacity))
    csvWriter.close()
  }

  def readData(): String = {
    val csvReader = CSVReader.open(outputFile)
    try {
      val rows = csvReader.all()
      rows.map(_.mkString(", ")).mkString("\n")
    } catch {
      case NonFatal(e) =>
        println(s"Error reading data from file: ${e.getMessage}")
        ""
    } finally {
      csvReader.close()
    }
  }

  def loadData(): Array[EnergyPowerSystem] = { // solar, wind hydro
    val energyPowerSystem = new EnergyPowerSystem()

    try {
      val csvReader = CSVReader.open(outputFile)
      val rows = csvReader.all()
      for (row <- rows.tail) { // Skip header row
        val cols = row.map(_.trim)
        if (cols.length == 5) {
          val Array(name, type_, date, energy, capacity) = cols.toArray
          val Array(currentCapacityStr, totalCapacityStr) = capacity.split("/")
          storageCapacity = currentCapacityStr.toInt
          defaultStorageCapacity = totalCapacityStr.toInt
          type_ match {
            case "H" =>
              val hydropowerPlant = new HydropowerPlant(name, energy.toInt)
              energyPowerSystem.addPlant(hydropowerPlant)
          }
        } else {
          println(s"Ignoring invalid row: ${cols.mkString(", ")}")
        }
      }
    } catch {
      case NonFatal(e) =>
        println(s"Error reading data from file: ${e.getMessage}")
    }

    Array(energyPowerSystem);
  }
}