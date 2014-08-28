package ch.infbr5.sentinel.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class DateFormater {

	private static SimpleDateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");

	private static SimpleDateFormat dfDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private static SimpleDateFormat dfWithDetailTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public static String formatToDate(Date date) {
		return dfDate.format(date);
	}

	public static String formatToDateWithTime(Date date) {
		return dfDateTime.format(date);
	}

	public static String formatToDateWithDetailTime(Date date) {
		return dfWithDetailTime.format(date);
	}

	public static String formatToDate(XMLGregorianCalendar date) {
		if (date == null) {
			return "";
		}
		return formatToDate(date.toGregorianCalendar().getTime());
	}

	public static String formatToDate(Calendar c) {
		if (c == null) {
			return "";
		}
		return formatToDate(c.getTime());
	}

	public static String formatToDateWithTime(XMLGregorianCalendar date) {
		if (date == null) {
			return "";
		}
		return formatToDateWithTime(date.toGregorianCalendar().getTime());
	}

	public static String formatToDateWithDetailTime(XMLGregorianCalendar date) {
		if (date == null) {
			return "";
		}
		return formatToDateWithDetailTime(date.toGregorianCalendar().getTime());
	}

}
