package com.example.demo.calendar;

import java.time.LocalDate;
import java.util.UUID;

public record CalendarEvent(
		UUID id,
		String title,
		LocalDate date,
		String timeText,
		String note) {
}
