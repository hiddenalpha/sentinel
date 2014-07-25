package ch.infbr5.sentinel.client.util;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {

	public static Date getCurrentDate() {
		return new Date();
	}

	public static XMLGregorianCalendar getCurrentXMLGregorianCalendar() {
		return XMLGregorianCalendarConverter.dateToXMLGregorianCalendar(getCurrentDate());
	}

}
