package menu

import event.{Event, EventContainer}
import file_controller.FileController
import glob_val.GlobalValues.{ANSI_GREEN, ANSI_RESET, storageCapacity}
import plant.HydropowerPlant
import sorter.Calculator.{calculateMeanEnergy, calculateMedianEnergy, calculateMidrangeEnergy, calculateModeEnergy, calculateRangeEnergy}
import sorter.EventSorter
import system.EnergyPowerSystem

import java.time.format.{DateTimeFormatter, DateTimeParseException}
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
    print("Plant number: ")

    def isValidChoice(input: String): Boolean = {
      Try(input.toInt).filter(choice => choice >= 1 && choice <= 5).isSuccess
    }

    try {
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
    } catch {
      case _: NumberFormatException =>
        println("Invalid input format. Please enter an integer.")
    }
  }

  def storageControlMenu(fileController: FileController, inputEvents: Array[Event]): Unit = { // Menu of the storage commands

    var isEventContainerEvents = false
    var events = inputEvents
    if (events == null) {
      EventContainer.reset()
      fileController.readEvents()
      events = EventContainer.events.toArray
      isEventContainerEvents = true
    }

    var list = "all" 
    if (!isEventContainerEvents) {
      list = "filtered"
    }
    println("The storage contains " + list + " energy operations of the system. ")
    println("Enter the number of the command what you need:")
    println()
    println(
      "1) Show " + list + " data from the storage.\n" +
        "2) Show Mean based on " + list + " data from the storage.\n" +
        "3) Show Median based on " + list + " data from the storage.\n" +
        "4) Show Mode based on " + list + " data from the storage.\n" +
        "5) Show Range based on " + list + " data from the storage.\n" +
        "6) Show Midrange based on " + list + " data from the storage.\n" +
        "7) Define filtered list.\n" +
        "Enter any other button to exit."
    )

    val input = scala.io.StdIn.readLine("YOUR OPTION: ")
    //    println(events.mkString("Array(", ", ", ")"))
    input match {
      case "1" => // show all
        val printedEvents = events.map(event => s"$ANSI_GREEN${event.toString}$ANSI_RESET").mkString("\n")
        println(printedEvents)
        storageControlMenu(fileController, events)
      case "2" => // mean
        val meanEnergy = calculateMeanEnergy(events) // Implement calculateMeanEnergy function
        println(ANSI_GREEN + s"Mean Energy: $meanEnergy" + ANSI_RESET)
        println("TEST")
        storageControlMenu(fileController, events)
      case "3" => // median
        val medianEnergy = calculateMedianEnergy(events) // Implement calculateMedianEnergy function
        println(ANSI_GREEN + s"Median Energy: $medianEnergy" + ANSI_RESET)
        storageControlMenu(fileController, events)
      case "4" => // mode
        val modeEnergy = calculateModeEnergy(events) // Implement calculateModeEnergy function
        println(ANSI_GREEN + s"Mode Energy: $modeEnergy" + ANSI_RESET)
        storageControlMenu(fileController, events)
      case "5" => // range
        val (minEnergy, maxEnergy) = calculateRangeEnergy(events) // Implement calculateRangeEnergy function
        println(ANSI_GREEN + s"Range of Energy: $minEnergy to $maxEnergy" + ANSI_RESET)
        storageControlMenu(fileController, events)
      case "6" => // midrange
        val midrangeEnergy = calculateMidrangeEnergy(events) // Implement calculateMidrangeEnergy function
        println(ANSI_GREEN + s"Midrange Energy: $midrangeEnergy" + ANSI_RESET)
        storageControlMenu(fileController, events)
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
              var dateInput = scala.io.StdIn.readLine("Enter date and time (yyyy-MM-dd HH): ")
              var dateTime = LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH"))
              var hourlyEvents = EventSorter.hourlySort(events,dateTime)
              hourlyEvents.map(event => s"$ANSI_GREEN${event.toString}$ANSI_RESET").foreach(println)
              storageControlMenu(fileController, hourlyEvents)
            case "2" =>
              var dateInput = scala.io.StdIn.readLine("Enter date (yyyy-MM-dd): ")
              var date = LocalDate.parse(dateInput)
              var dailyEvents = EventSorter.dailySort(events,date)
              dailyEvents.map(event => s"$ANSI_GREEN${event.toString}$ANSI_RESET").foreach(println)
              storageControlMenu(fileController, dailyEvents)
            case "3" =>
              var dateInput = scala.io.StdIn.readLine("Enter date (yyyy-MM-dd): ")
              var date = LocalDate.parse(dateInput)
              var weeklyEvents = EventSorter.weeklySort(events,date)
              weeklyEvents.map(event => s"$ANSI_GREEN${event.toString}$ANSI_RESET").foreach(println)
              storageControlMenu(fileController, weeklyEvents)
            case "4" =>
              var dateInput = scala.io.StdIn.readLine("Enter date (yyyy-MM): ")
              var date = LocalDate.parse(dateInput + "-01")
              var monthlyEvents = EventSorter.monthlySort(events,date)
              monthlyEvents.map(event => s"$ANSI_GREEN${event.toString}$ANSI_RESET").foreach(println)
              storageControlMenu(fileController, monthlyEvents)
            case _ =>
              println("Invalid option.")
          }
        } catch {
          case e: DateTimeParseException =>
            println("Invalid date format. Please enter the date in the format yyyy-MM-dd HH:mm for option 1 or yyyy-MM-dd for options 2, 3, and 4.")
          case _: Throwable =>
            println("An unexpected error occurred.")
        }
      case _ =>
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
        "4) Control storage and data analysis\n" +
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
      case _ => // nothing
    }
  }

  def dataControlMenu(fileController: FileController): Unit = { // Menu of the data analysis commands

  }


}
