package menu

import event.{Event, EventContainer}
import file_controller.FileController
import glob_val.GlobalValues.{ANSI_GREEN, ANSI_RESET, storageCapacity}
import plant.HydropowerPlant
import sorter.EventSorter
import system.EnergyPowerSystem

import java.time.format.DateTimeParseException
import java.time.{LocalDate, LocalDateTime}
import scala.util.Try
import scala.util.control.Breaks.{break, breakable}

object Menus {
  def hydropowerControlMenu(system: EnergyPowerSystem, fileController: FileController): Unit = {
    println("Choose Hydropower plant:")
    val plants = system.plants
    for ((plant, index) <- plants.zipWithIndex) {
      println(s"${index + 1}. ${plant.plantName}")
    }
    print("plant.Plant number: ")

    def isValidChoice(input: String): Boolean = {
      Try(input.toInt).filter(choice => choice >= 1 && choice <= 5).isSuccess
    }

    val userPlantInput = scala.io.StdIn.readInt()

    breakable {
      while (userPlantInput >= 1 && userPlantInput <= plants.length) {

        val selectedPlant: HydropowerPlant = plants(userPlantInput - 1).asInstanceOf[HydropowerPlant]


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
              storageCapacity = storageCapacity - generatedEnergy
              fileController.writedata(selectedPlant.plantName, "H", LocalDateTime.now(), generatedEnergy, selectedPlant.quality)
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

  def storageControlMenu(fileController: FileController, filteredEvents: Option[Array[Event]]): Unit = { // Menu of the storage commands
    fileController.readEvents()
    val events = filteredEvents.getOrElse(EventContainer.events.toArray)
    val isEventContainerEvents = events eq EventContainer.events.toArray
    var list = "all"
    if (isEventContainerEvents) {
      list = "filtered"
    }
    println("The storage contains all energy operations of the system. ")
    println("Enter the number of the command what you need:")
    println()
    println(
      "1) Show all data from the storage.\n" +
        "2) Show Mean based on " + list + " data from the storage.\n" +
        "3) Show Median based on " + list + " data from the storage.\n" +
        "4) Show Mode based on " + list + " data from the storage.\n" +
        "5) Show Range based on " + list + " data from the storage.\n" +
        "6) Show Midrange based on " + list + " data from the storage.\n" +
        "7) Define filtered list.\n"
    )

    val input = scala.io.StdIn.readLine("YOUR OPTION: ")
    input match {
      case "1" => // show all
        print(ANSI_GREEN + fileController.readData() + ANSI_RESET)
        println()
        storageControlMenu(fileController, null)
      case "2" => // mean
      case "3" => // median
      case "4" => // mode
      case "5" => // range
      case "6" => // midrange
      case "7" => // filter/sort
        println(
          "1) Define hourly filter.\n" +
            "2) Define daily filter.\n" +
            "3) Define weekly filter.\n" +
            "4) Define monthly filter.\n"
        )
        val filIn = scala.io.StdIn.readLine("YOUR OPTION: ")
        try {
          filIn match {
            case "1" =>
              val dateInput = scala.io.StdIn.readLine("Enter date and time (yyyy-MM-dd HH:mm): ")
              val dateTime = LocalDateTime.parse(dateInput)
              val hourlyEvents = EventSorter.hourlySort(dateTime)
              hourlyEvents.foreach(println)
              storageControlMenu(fileController, Option(hourlyEvents))
            case "2" =>
              val dateInput = scala.io.StdIn.readLine("Enter date (yyyy-MM-dd): ")
              val date = LocalDate.parse(dateInput)
              val dailyEvents = EventSorter.dailySort(date)
              dailyEvents.foreach(println)
              storageControlMenu(fileController, Option(dailyEvents))
            case "3" =>
              val dateInput = scala.io.StdIn.readLine("Enter date (yyyy-MM-dd): ")
              val date = LocalDate.parse(dateInput)
              val weeklyEvents = EventSorter.weeklySort(date)
              weeklyEvents.foreach(println)
              storageControlMenu(fileController, Option(weeklyEvents))
            case "4" =>
              val dateInput = scala.io.StdIn.readLine("Enter date (yyyy-MM): ")
              val date = LocalDate.parse(dateInput + "-01")
              val monthlyEvents = EventSorter.monthlySort(date)
              monthlyEvents.foreach(println)
              storageControlMenu(fileController, Option(monthlyEvents))
            case _ =>
              println("Invalid option.")
          }
        } catch {
          case e: DateTimeParseException =>
            println("Invalid date format. Please enter the date in the format yyyy-MM-dd HH:mm for option 1 or yyyy-MM-dd for options 2, 3, and 4.")
          case _: Throwable =>
            println("An unexpected error occurred.")
        }
        storageControlMenu(fileController, null)
    }

  }

  def menu(fileController: FileController, hydropowerSystem: EnergyPowerSystem, solarpowerSystem: EnergyPowerSystem, windpowerSystem: EnergyPowerSystem): Unit = {
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
        menu(fileController, hydropowerSystem, solarpowerSystem, windpowerSystem)
      case "2" =>
        // wind
        menu(fileController, hydropowerSystem, solarpowerSystem, windpowerSystem)
      case "3" =>
        hydropowerControlMenu(hydropowerSystem, fileController)
        menu(fileController, hydropowerSystem, solarpowerSystem, windpowerSystem)
      case "4" =>
        storageControlMenu(fileController, null)
        menu(fileController, hydropowerSystem, solarpowerSystem, windpowerSystem)
      case "5" =>
        dataControlMenu(fileController)
        menu(fileController, hydropowerSystem, solarpowerSystem, windpowerSystem)
      case _ => // nothing
    }
  }

  def dataControlMenu(fileController: FileController): Unit = { // Menu of the data analysis commands

  }


}
