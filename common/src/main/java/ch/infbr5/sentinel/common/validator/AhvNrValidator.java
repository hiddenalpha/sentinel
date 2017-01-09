package ch.infbr5.sentinel.common.validator;

public class AhvNrValidator implements CommonValidator {

	@Override
	public boolean validate(String nr) {
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
