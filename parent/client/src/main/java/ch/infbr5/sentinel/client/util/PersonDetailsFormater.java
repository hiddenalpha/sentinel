package ch.infbr5.sentinel.client.util;

import ch.infbr5.sentinel.client.wsgen.PersonDetails;

public class PersonDetailsFormater {

	public static String getFullName(PersonDetails personDetails) {
		if (personDetails == null) {
			return "";
		}
		return personDetails.getGrad() + ". " + personDetails.getName() + " " + personDetails.getVorname();
	}

}
