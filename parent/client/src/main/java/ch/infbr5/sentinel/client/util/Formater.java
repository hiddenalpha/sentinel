package ch.infbr5.sentinel.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class Formater {

	private static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	private static SimpleDateFormat dfWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static SimpleDateFormat dfWithDetailTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public static String format(Date date) {
		return df.format(date);
	}

	public static String formatWithTime(Date date) {
		return dfWithTime.format(date);
	}

	public static String formatWithDetailTime(Date date) {
		return dfWithDetailTime.format(date);
	}

	public static String format(XMLGregorianCalendar date) {
		return format(date.toGregorianCalendar().getTime());
	}

	public static String formatWithTime(XMLGregorianCalendar date) {
		return formatWithTime(date.toGregorianCalendar().getTime());
	}

	public static String formatWithDetailTime(XMLGregorianCalendar date) {
		return formatWithDetailTime(date.toGregorianCalendar().getTime());
	}

}
