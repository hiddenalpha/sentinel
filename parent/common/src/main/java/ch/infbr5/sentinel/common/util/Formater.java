package ch.infbr5.sentinel.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class Formater {

	private static SimpleDateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");
	private static SimpleDateFormat dfDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private static SimpleDateFormat dfWithDetailTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public static String format(Date date) {
		return dfDate.format(date);
	}

	public static String formatWithTime(Date date) {
		return dfDateTime.format(date);
	}

	public static String formatWithDetailTime(Date date) {
		return dfWithDetailTime.format(date);
	}

	public static String format(XMLGregorianCalendar date) {
		return format(date.toGregorianCalendar().getTime());
	}

	public static String formatWithTime(XMLGregorianCalendar date) {
		if (date == null) {
			return "";
		}
		return formatWithTime(date.toGregorianCalendar().getTime());
	}

	public static String formatWithDetailTime(XMLGregorianCalendar date) {
		return formatWithDetailTime(date.toGregorianCalendar().getTime());
	}

	public static String formatDateTime(Date date) {
		return dfDateTime.format(date);
	}

	public static String formatDateTime(XMLGregorianCalendar date) {
		return formatDateTime(date.toGregorianCalendar().getTime());
	}

}
