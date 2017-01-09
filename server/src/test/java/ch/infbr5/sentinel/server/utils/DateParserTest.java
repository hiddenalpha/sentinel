package ch.infbr5.sentinel.server.utils;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import ch.infbr5.sentinel.common.util.DateFormater;

public class DateParserTest {

	@Test
	public void testParseDateStringToDate() throws ParseException   {
		Date date = DateParser.parseDateStringToDate("15.08.2014");
		Assert.assertEquals("15.08.2014", DateFormater.formatToDate(date));
	}

}
