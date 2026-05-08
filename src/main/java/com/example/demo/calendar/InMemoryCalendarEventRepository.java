package com.example.demo.calendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCalendarEventRepository {

	private final Map<UUID, CalendarEvent> store = new ConcurrentHashMap<>();

	public List<CalendarEvent> findByMonth(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		LocalDate start = ym.atDay(1);
		LocalDate end = ym.atEndOfMonth();
		return store.values().stream()
				.filter(e -> !e.date().isBefore(start) && !e.date().isAfter(end))
				.sorted(Comparator.comparing(CalendarEvent::date).thenComparing(CalendarEvent::title))
				.toList();
	}

	public void save(CalendarEvent event) {
		store.put(event.id(), event);
	}

	public boolean delete(UUID id) {
		return store.remove(id) != null;
	}
}
