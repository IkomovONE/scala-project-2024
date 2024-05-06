package sorter

import event.Event

import java.time.{LocalDate, LocalDateTime}

object EventSorter {
  def dailySort(events: Array[Event], date: LocalDate): Array[Event] = {
    events.filter(_.date.toLocalDate == date)
  }

  def hourlySort(events: Array[Event], dateTime: LocalDateTime): Array[Event] = {
    val hour = dateTime.getHour
    events.filter(event =>
      event.date.toLocalDate == dateTime.toLocalDate && event.date.getHour == hour
    )
  }

  def monthlySort(events: Array[Event], date: LocalDate): Array[Event] = {
    val startOfMonth = date.withDayOfMonth(1)
    val endOfMonth = date.withDayOfMonth(date.lengthOfMonth())
    events.filter(event =>
      !event.date.toLocalDate.isBefore(startOfMonth) && !event.date.toLocalDate.isAfter(endOfMonth)
    )
  }

  def weeklySort(events: Array[Event], date: LocalDate): Array[Event] = {
    val startOfWeek = date.minusDays(date.getDayOfWeek.getValue - 1)
    val endOfWeek = date.plusDays(7 - date.getDayOfWeek.getValue)
    events.filter(event =>
      !event.date.toLocalDate.isBefore(startOfWeek) && !event.date.toLocalDate.isAfter(endOfWeek)
    )
  }
}