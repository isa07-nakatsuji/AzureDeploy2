package com.example.demo.calendar;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayCell(
		LocalDate date,
		boolean inCurrentMonth,
		List<CalendarEvent> events) {
}
