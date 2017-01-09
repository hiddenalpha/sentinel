package ch.infbr5.sentinel.server.ws.importer.modification;

import ch.infbr5.sentinel.server.ws.PersonDetails;

public class ModificationNewPerson extends Modification {

	private PersonDetails personDetails;

	public PersonDetails getPersonDetails() {
		return personDetails;
	}

	public void setPersonDetails(PersonDetails personDetails) {
		this.personDetails = personDetails;
	}

}
