package com.example.demo.calendar;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EventForm {

	@NotBlank(message = "タイトルを入力してください")
	@Size(max = 120, message = "タイトルは120文字以内にしてください")
	private String title;

	@NotNull(message = "日付を選んでください")
	private LocalDate date;

	@Size(max = 32, message = "時刻は32文字以内にしてください")
	private String timeText = "";

	@Size(max = 500, message = "メモは500文字以内にしてください")
	private String note = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getTimeText() {
		return timeText;
	}

	public void setTimeText(String timeText) {
		this.timeText = timeText;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
