package glob_val

object GlobalValues {
  var ANSI_RESET = "\u001B[0m"
  var ANSI_GREEN = "\u001B[32m"
  var defaultStorageCapacity = 1000
  var storageCapacity = 1000


  def setDefaultStorageCapacity(newCapacity: Int): Unit = {
    defaultStorageCapacity = newCapacity
  }

  def setStorageCapacity(newCapacity: Int): Unit = {
    storageCapacity = newCapacity
  }
}
