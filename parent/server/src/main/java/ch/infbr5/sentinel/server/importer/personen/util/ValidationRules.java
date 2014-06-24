package ch.infbr5.sentinel.server.importer.personen.util;

import java.text.ParseException;

import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.utils.DateHelper;

public class ValidationRules {

	public static boolean isValidGeburtstag(String string) {
		if (string == null) {
			return false;
		}

		 // fix Ahv Nr sind keine Geburtstage
		if (isValidAhvNr(string)) {
			return false;
		}

		boolean isValid = true;
		try {
			DateHelper.getCalendar(string);
		} catch (ParseException e) {
			isValid = false;
		}
		return isValid;
	}

	public static boolean isValidGrad(String string) {
		if (string == null) {
			return false;
		}

		if (Grad.getGrad(string) == null) {
			return false;
		}
		return true;
	}

	public static boolean isValidString(String string) {
		return string != null && !"".equals(string);
	}

	public static boolean isValidAhvNr(String nr) {
		int checksum = 0;

		if (nr == null) {
			return false;
		}

		if (nr.length() != 16)
			return false;

		try {
			checksum += Character.getNumericValue(nr.charAt(0));
			checksum += Character.getNumericValue(nr.charAt(1)) * 3;
			checksum += Character.getNumericValue(nr.charAt(2));
			if (!String.valueOf(nr.charAt(3)).equals("."))
				return false;

			checksum += Character.getNumericValue(nr.charAt(4)) * 3;
			checksum += Character.getNumericValue(nr.charAt(5));
			checksum += Character.getNumericValue(nr.charAt(6)) * 3;
			checksum += Character.getNumericValue(nr.charAt(7));
			if (!String.valueOf(nr.charAt(8)).equals("."))
				return false;

			checksum += Character.getNumericValue(nr.charAt(9)) * 3;
			checksum += Character.getNumericValue(nr.charAt(10));
			checksum += Character.getNumericValue(nr.charAt(11)) * 3;
			checksum += Character.getNumericValue(nr.charAt(12));
			if (!String.valueOf(nr.charAt(13)).equals("."))
				return false;

			checksum += Character.getNumericValue(nr.charAt(14)) * 3;
			int pruefziffer = Character.getNumericValue(nr.charAt(15));

			return ((checksum + pruefziffer) % 10 == 0);

		} catch (NumberFormatException ex) {
			return false;
		}
	}

}
