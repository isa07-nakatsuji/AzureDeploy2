package com.example.demo.calendar;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

	private final CalendarService calendarService;

	private static final List<String> WEEKDAY_LABELS = List.of(
			java.time.DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
			java.time.DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
			java.time.DayOfWeek.TUESDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
			java.time.DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
			java.time.DayOfWeek.THURSDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
			java.time.DayOfWeek.FRIDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN),
			java.time.DayOfWeek.SATURDAY.getDisplayName(TextStyle.SHORT, Locale.JAPAN));

	public CalendarController(CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	@GetMapping
	public String month(
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) LocalDate selected,
			Model model) {
		LocalDate today = LocalDate.now();
		int y = year != null ? year : today.getYear();
		int m = month != null ? month : today.getMonthValue();

		if (m < 1 || m > 12) {
			m = today.getMonthValue();
		}

		EventForm eventForm = new EventForm();
		eventForm.setDate(selected != null ? selected : today);

		model.addAttribute("year", y);
		model.addAttribute("month", m);
		model.addAttribute("monthTitle", calendarService.formatMonthTitle(y, m));
		model.addAttribute("weeks", calendarService.buildMonthGrid(y, m));
		model.addAttribute("weekdayLabels", WEEKDAY_LABELS);
		model.addAttribute("eventForm", eventForm);
		model.addAttribute("today", today);
		Map<String, Integer> adj = calendarService.adjacentMonths(y, m);
		model.addAllAttributes(adj);
		return "calendar";
	}

	@PostMapping("/events")
	public String addEvent(
			@RequestParam int redirectYear,
			@RequestParam int redirectMonth,
			@Valid @ModelAttribute("eventForm") EventForm eventForm,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			int y = redirectYear;
			int mo = redirectMonth;
			LocalDate today = LocalDate.now();
			model.addAttribute("year", y);
			model.addAttribute("month", mo);
			model.addAttribute("monthTitle", calendarService.formatMonthTitle(y, mo));
			model.addAttribute("weeks", calendarService.buildMonthGrid(y, mo));
			model.addAttribute("weekdayLabels", WEEKDAY_LABELS);
			model.addAttribute("today", today);
			model.addAllAttributes(calendarService.adjacentMonths(y, mo));
			return "calendar";
		}
		calendarService.addEvent(eventForm);
		return "redirect:/calendar?year=" + redirectYear + "&month=" + redirectMonth;
	}

	@PostMapping("/events/delete")
	public String deleteEvent(
			@RequestParam UUID id,
			@RequestParam int redirectYear,
			@RequestParam int redirectMonth) {
		calendarService.deleteEvent(id);
		return "redirect:/calendar?year=" + redirectYear + "&month=" + redirectMonth;
	}
}
