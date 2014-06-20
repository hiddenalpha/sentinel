package ch.infbr5.sentinel.server.ws.importer.modification;

import ch.infbr5.sentinel.server.ws.PersonDetails;

public class ModificationUpdatePerson extends Modification {

	private PersonDetails personDetailsOld;
	
	private PersonDetails personDetailsNew;
	
	private UpdatePersonAttributeDiff[] updatePersonenDiffs;

	public PersonDetails getPersonDetailsOld() {
		return personDetailsOld;
	}

	public void setPersonDetailsOld(PersonDetails personDetailsOld) {
		this.personDetailsOld = personDetailsOld;
	}
	
	public PersonDetails getPersonDetailsNew() {
		return personDetailsNew;
	}

	public void setPersonDetailsNew(PersonDetails personDetailsNew) {
		this.personDetailsNew = personDetailsNew;
	}

	public UpdatePersonAttributeDiff[] getUpdatePersonenDiffs() {
		return updatePersonenDiffs;
	}

	public void setUpdatePersonenDiffs(UpdatePersonAttributeDiff[] updatePersonenDiffs) {
		this.updatePersonenDiffs = updatePersonenDiffs;
	}
	
}
