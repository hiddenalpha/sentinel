package ch.infbr5.sentinel.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public static String getFormatedString(Date d) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormat.format(d);
	}

	public static String getFormatedString(Calendar c) {
		return getFormatedString(c.getTime());
	}

	public static Date getDate(String t) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormat.parse(t);
	}

	public static Calendar getCalendar(String t) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate(t));
		return cal;
	}
}
