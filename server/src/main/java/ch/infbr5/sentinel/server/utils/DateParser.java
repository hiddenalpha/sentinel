package ch.infbr5.sentinel.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {

	private DateParser() {

	}

	public static Date parseDateStringToDate(String t) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormat.parse(t);
	}

	public static Calendar parseDateStringToCalendar(String t) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parseDateStringToDate(t));
		return cal;
	}
}
