package sorter

import event.{Event, EventContainer}
import event.EventContainer.events

import java.time.{LocalDate, LocalDateTime}

object EventSorter {
  import EventContainer._

  def dailySort(date: LocalDate): Array[Event] = {
    events.filter(_.date.toLocalDate == date).toArray
  }

  def hourlySort(dateTime: LocalDateTime): Array[Event] = {
    val hour = dateTime.getHour
    events.filter(event =>
      event.date.toLocalDate == dateTime.toLocalDate && event.date.getHour == hour
    ).toArray
  }

  def monthlySort(date: LocalDate): Array[Event] = {
    val startOfMonth = date.withDayOfMonth(1)
    val endOfMonth = date.withDayOfMonth(date.lengthOfMonth())
    events.filter(event =>
      !event.date.toLocalDate.isBefore(startOfMonth) && !event.date.toLocalDate.isAfter(endOfMonth)
    ).toArray
  }

  def weeklySort(date: LocalDate): Array[Event] = {
    val startOfWeek = date.minusDays(date.getDayOfWeek.getValue - 1)
    val endOfWeek = date.plusDays(7 - date.getDayOfWeek.getValue)
    events.filter(event =>
      !event.date.toLocalDate.isBefore(startOfWeek) && !event.date.toLocalDate.isAfter(endOfWeek)
    ).toArray
  }
}