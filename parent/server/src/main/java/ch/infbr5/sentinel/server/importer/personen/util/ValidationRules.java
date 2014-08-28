package ch.infbr5.sentinel.server.importer.personen.util;

import java.text.ParseException;

import ch.infbr5.sentinel.common.validator.AhvNrValidator;
import ch.infbr5.sentinel.common.validator.CommonValidator;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.utils.DateParser;

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
			DateParser.parseDateStringToCalendar(string);
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
		CommonValidator validator = new AhvNrValidator();
		return validator.validate(nr);
	}

}
