package filter

object Filter {
  var Daily: Option[String] = None
  var Hourly: Option[(String, Int)] = None
  var Monthly: Option[String] = None

  def setDailyFilter(day: String): Unit = {
    Daily = Some(day)
  }

  def setHourlyFilter(day: String, hour: Int): Unit = {
    Hourly = Some((day, hour))
  }

  def setMonthlyFilter(month: String): Unit = {
    Monthly = Some(month)
  }
}
