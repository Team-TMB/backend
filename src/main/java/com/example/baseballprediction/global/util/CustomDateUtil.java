package com.example.baseballprediction.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateUtil {
	public static LocalDateTime toDateTime(String dateTime) {

		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}

	public static String dateToString(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	public static String dateToString(LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	public static LocalDate parseLocalDate(LocalDateTime dateTime) {
		return dateTime.toLocalDate();
	}

	public static String dateToYearMonth(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofPattern("yyyyMM")).toString();
	}

	public static LocalDate stringToDate(String date) {
		return LocalDate.parse(date);
	}
}
