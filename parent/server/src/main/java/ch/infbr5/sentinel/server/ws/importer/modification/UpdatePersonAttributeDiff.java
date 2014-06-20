package ch.infbr5.sentinel.server.ws.importer.modification;

import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenAttribute;

public class UpdatePersonAttributeDiff {

	private PersonenAttribute personenAttribute;
	
	private String oldValue;
	
	private String newValue;

	public PersonenAttribute getPersonenAttribute() {
		return personenAttribute;
	}

	public void setPersonenAttribute(PersonenAttribute personenAttribute) {
		this.personenAttribute = personenAttribute;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
}
