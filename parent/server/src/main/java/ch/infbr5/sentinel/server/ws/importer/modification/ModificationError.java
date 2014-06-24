package ch.infbr5.sentinel.server.ws.importer.modification;

import ch.infbr5.sentinel.server.ws.PersonDetails;

public class ModificationError extends Modification {

	private String errorMessage;

	private PersonDetails personDetails;

	@Override
	public boolean isToModify() {
		return false;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public PersonDetails getPersonDetails() {
		return personDetails;
	}

	public void setPersonDetails(PersonDetails personDetails) {
		this.personDetails = personDetails;
	}

}
