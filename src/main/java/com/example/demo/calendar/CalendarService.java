package com.example.demo.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class CalendarService {

	private static final WeekFields JP_WEEK = WeekFields.of(Locale.JAPAN);
	private static final DateTimeFormatter MONTH_TITLE = DateTimeFormatter.ofPattern("yyyy年M月", Locale.JAPAN);

	private final InMemoryCalendarEventRepository repository;

	public CalendarService(InMemoryCalendarEventRepository repository) {
		this.repository = repository;
	}

	public String formatMonthTitle(int year, int month) {
		return YearMonth.of(year, month).atDay(1).format(MONTH_TITLE);
	}

	public List<List<CalendarDayCell>> buildMonthGrid(int year, int month) {
		YearMonth ym = YearMonth.of(year, month);
		LocalDate firstOfMonth = ym.atDay(1);
		LocalDate lastOfMonth = ym.atEndOfMonth();

		DayOfWeek firstDayOfWeek = JP_WEEK.getFirstDayOfWeek();
		LocalDate gridStart = firstOfMonth.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));

		LocalDate lastWeekStart = lastOfMonth.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
		LocalDate gridEnd = lastWeekStart.plusDays(6);

		Map<LocalDate, List<CalendarEvent>> byDate = repository.findByMonth(year, month).stream()
				.collect(Collectors.groupingBy(CalendarEvent::date));

		List<List<CalendarDayCell>> weeks = new ArrayList<>();
		List<CalendarDayCell> row = new ArrayList<>();
		for (LocalDate d = gridStart; !d.isAfter(gridEnd); d = d.plusDays(1)) {
			boolean inMonth = !d.isBefore(firstOfMonth) && !d.isAfter(lastOfMonth);
			List<CalendarEvent> events = byDate.getOrDefault(d, List.of());
			row.add(new CalendarDayCell(d, inMonth, events));
			if (d.get(JP_WEEK.dayOfWeek()) == 7) {
				weeks.add(List.copyOf(row));
				row.clear();
			}
		}
		if (!row.isEmpty()) {
			weeks.add(row);
		}
		return weeks;
	}

	public Map<String, Integer> adjacentMonths(int year, int month) {
		YearMonth current = YearMonth.of(year, month);
		YearMonth prev = current.minusMonths(1);
		YearMonth next = current.plusMonths(1);
		Map<String, Integer> m = new HashMap<>();
		m.put("prevYear", prev.getYear());
		m.put("prevMonth", prev.getMonthValue());
		m.put("nextYear", next.getYear());
		m.put("nextMonth", next.getMonthValue());
		return m;
	}

	public CalendarEvent addEvent(EventForm form) {
		String time = form.getTimeText() == null ? "" : form.getTimeText().trim();
		String note = form.getNote() == null ? "" : form.getNote().trim();
		CalendarEvent event = new CalendarEvent(
				UUID.randomUUID(),
				form.getTitle().trim(),
				form.getDate(),
				time,
				note);
		repository.save(event);
		return event;
	}

	public boolean deleteEvent(UUID id) {
		return repository.delete(id);
	}
}
