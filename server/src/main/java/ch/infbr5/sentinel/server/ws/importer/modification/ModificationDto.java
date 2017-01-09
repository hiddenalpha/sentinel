package ch.infbr5.sentinel.server.ws.importer.modification;


public class ModificationDto extends Modification {

	private ModificationNewPerson[] modificationNewPersons;

	private ModificationUpdatePerson[] modificationUpdatePersons;

	private ModificationArchivePerson[] modificationArchivePersons;

	private ModificationUpdatePersonAndNewAusweis[] modificationNewAusweise;

	private ModificationError[] modificationErrors;

	public ModificationError[] getModificationErrors() {
		if (modificationErrors == null) {
			modificationErrors = new ModificationError[0];
		}
		return modificationErrors;
	}

	public void setModificationErrors(ModificationError[] modificationErrors) {
		this.modificationErrors = modificationErrors;
	}

	public ModificationUpdatePersonAndNewAusweis[] getModificationNewAusweise() {
		if (modificationNewAusweise == null) {
			modificationNewAusweise = new ModificationUpdatePersonAndNewAusweis[0];
		}
		return modificationNewAusweise;
	}

	public void setModificationNewAusweise(
			ModificationUpdatePersonAndNewAusweis[] modificationNewAusweise) {
		this.modificationNewAusweise = modificationNewAusweise;
	}

	public ModificationNewPerson[] getModificationNewPersons() {
		if (modificationNewPersons == null) {
			modificationNewPersons = new ModificationNewPerson[0];
		}
		return modificationNewPersons;
	}

	public void setModificationNewPersons(
			ModificationNewPerson[] modificationNewPersons) {
		this.modificationNewPersons = modificationNewPersons;
	}

	public ModificationUpdatePerson[] getModificationUpdatePersons() {
		if (modificationUpdatePersons == null) {
			modificationUpdatePersons = new ModificationUpdatePerson[0];
		}
		return modificationUpdatePersons;
	}

	public void setModificationUpdatePersons(
			ModificationUpdatePerson[] modificationUpdatePersons) {
		this.modificationUpdatePersons = modificationUpdatePersons;
	}

	public ModificationArchivePerson[] getModificationArchivePersons() {
		if (modificationArchivePersons == null) {
			modificationArchivePersons = new ModificationArchivePerson[0];
		}
		return modificationArchivePersons;
	}

	public void setModificationArchivePersons(
			ModificationArchivePerson[] modificationArchivePersons) {
		this.modificationArchivePersons = modificationArchivePersons;
	}

	public boolean hasModifications() {
		if (getModificationArchivePersons().length == 0 && getModificationNewAusweise().length == 0 && getModificationNewPersons().length == 0 && getModificationUpdatePersons().length == 0) {
			return false;
		}
		return true;
	}

}
