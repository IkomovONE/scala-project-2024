import scala.util.{Failure, Success, Try}
import java.io.{File, IOException}
import com.github.tototoshi.csv._

import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._
import scala.util.control.NonFatal

object Main {

  val ANSI_RESET = "\u001B[0m"
  val ANSI_GREEN = "\u001B[32m"

  object GlobalValues {
    val defaultStorageCapacity = 1000
    var storageCapacity = 1000
  }

  class Plant(val plantName: String, generatedEnergy: Int) {
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

  class FileController(filePath: String) {
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
      val formattedCapacity = s"${GlobalValues.storageCapacity}/${GlobalValues.defaultStorageCapacity}"
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
  }

  class HydropowerPlant(plantName: String, var generatedEnergy: Int) extends Plant(plantName, generatedEnergy) {

    var turbineSpeed = 100

    def increaseTurbineSpeed: Unit = {
      turbineSpeed = turbineSpeed + 10
      println(ANSI_GREEN + "Increase turbine speed on 10%." + ANSI_RESET)
    }

    def reduceTurbineSpeed: Unit = {
      turbineSpeed = turbineSpeed - 10
      println(ANSI_GREEN + "Reduce turbine speed on 10%." + ANSI_RESET)
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

    override def generateEnergy(): Int = {
      generatedEnergy = generatedEnergy * quality / 100 * turbineSpeed / 100
      decrementQuality()
      totalGeneratedEnergy = totalGeneratedEnergy + generatedEnergy
      generatedEnergy
    }
  }

  // fix error with non int input
  def hydropowerControlMenu(system: HydropowerSystem, fileController: FileController): Unit = {
    println("Choose Hydropower plant:")
    val plants = system.hydropowerPlants
    for ((plant, index) <- plants.zipWithIndex) {
      println(s"${index + 1}. ${plant.plantName}")
    }
    print("Plant number: ")

    def isValidChoice(input: String): Boolean = {
      Try(input.toInt).filter(choice => choice >= 1 && choice <= 5).isSuccess
    }

    val userPlantInput = scala.io.StdIn.readInt()

    breakable {
      while (userPlantInput >= 1 && userPlantInput <= plants.length) {
        val selectedPlant: HydropowerPlant = plants(userPlantInput - 1)

        println(s"Choose ${selectedPlant.plantName} command option :\n" +
          "1) Check quality.\n" +
          "2) Generate Energy.\n" +
          "3) Increase turbine speed.\n" +
          "4) Reduce turbine speed.\n" +
          "5) Disconnect plant.\n" +
          "Enter any other button to exit.")
        print("Command: ")

        val userChoiceInput = scala.io.StdIn.readLine()

        if (isValidChoice(userChoiceInput)) {
          val userChoice = userChoiceInput.toInt
          userChoice match {
            case 1 => // Check quality
              println(s"${ANSI_GREEN}Quality of plant: ${selectedPlant.plantName} is ${selectedPlant.quality}. ${ANSI_RESET}")
            case 2 => // Generate Energy

              var generatedEnergy: Int = selectedPlant.generateEnergy()
              GlobalValues.storageCapacity = GlobalValues.storageCapacity - generatedEnergy
              fileController.writedata(selectedPlant.plantName, "H", LocalDate.now(), generatedEnergy)
              println(s"${ANSI_GREEN}Generated energy of plant: ${selectedPlant.plantName} is ${generatedEnergy}. ${ANSI_RESET}")
            case 3 => //Increase turbine speed
              selectedPlant.increaseTurbineSpeed
              println(s"${ANSI_GREEN}Increased turbine speed of plant: ${selectedPlant.plantName}. ${ANSI_RESET}")
            case 4 => //Reduce turbine speed
              selectedPlant.reduceTurbineSpeed
              println(s"${ANSI_GREEN}Reduced turbine speed of plant: ${selectedPlant.plantName}. ${ANSI_RESET}")
            case 5 => //Disconnect plant
              //
              system.disconnect(selectedPlant)
              println(s"${ANSI_GREEN}Disconnected plant: ${selectedPlant.plantName} from the system. ${ANSI_RESET}")
              println("")
              break()

          }
        } else {
          break()
        }
        println("")
      }
    }
  }

  def storageControlMenu(fileController: FileController): Unit = { // Menu of the storage commands
    println("The storage contains all energy operations of the system. ")
    println("Enter the number of the command what you need:")
    println(
      "1) Show all data from the storage.\n" +
        "2) Clear data\n")

    val input = scala.io.StdIn.readLine("YOUR OPTION: ")
    input match {
      case "1" => // show all
        print(ANSI_GREEN+fileController.readData()+ANSI_RESET)
        println()
        storageControlMenu(fileController)
      case "2" => // clear

        storageControlMenu(fileController)
    }

  }

  def dataControlMenu(fileController: FileController): Unit = { // Menu of the data analysis commands

  }

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

  def menu(fileController: FileController, hydropowerSystem: HydropowerSystem): Unit = {
    // main menu options
    println("THIS IS REPS COMMANDS CONTROL")
    println("Enter the number of the command what you need:")
    println(
      "1) Control solar plants.\n" +
        "2) Control wind plants.\n" +
        "3) Control hydro plants.\n" +
        "4) Control storage\n" +
        "5) Manage data analysis.\n" +
        "Enter any other button to exit.")
    val input = scala.io.StdIn.readLine("YOUR OPTION: ")
    input match {
      case "1" =>
        // solar
        menu(fileController, hydropowerSystem)
      case "2" =>
        // wind
        menu(fileController, hydropowerSystem)
      case "3" =>
        hydropowerControlMenu(hydropowerSystem, fileController)
        menu(fileController, hydropowerSystem)
      case "4" =>
        storageControlMenu(fileController)
        menu(fileController, hydropowerSystem)
      case "5" =>
        dataControlMenu(fileController)
        menu(fileController, hydropowerSystem)
      case _ => // nothing
    }
  }

  def main(args: Array[String]): Unit = {

    val fileController = new FileController("data.csv")

    val hydropowerSystem = new HydropowerSystem

    val hydropowerPlant1 = new HydropowerPlant("HPP-1", 100)
    val hydropowerPlant2 = new HydropowerPlant("HPP-2", 200)
    val hydropowerPlant3 = new HydropowerPlant("HPP-3", 300)

    hydropowerSystem.addPlant(hydropowerPlant1)
    hydropowerSystem.addPlant(hydropowerPlant2)
    hydropowerSystem.addPlant(hydropowerPlant3)


    menu(fileController, hydropowerSystem)
  }
}