package ch.infbr5.sentinel.server.importer.personen.state;

import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumn;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumnMapping;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationDto;

public class PersonenImporterState {

	private boolean isKompletterBestand;

	private String filenameData;

	private PersonenImportColumn[] columns;

	private PersonenImportColumnMapping[] mappingColumns;

	private ModificationDto modification;

	public ModificationDto getModifications() {
		return modification;
	}

	public void setModifications(ModificationDto modification) {
		this.modification = modification;
	}

	public boolean isKompletterBestand() {
		return isKompletterBestand;
	}

	public void setKompletterBestand(boolean isKompletterBestand) {
		this.isKompletterBestand = isKompletterBestand;
	}

	public void setFilenameData(String filenameData) {
		this.filenameData = filenameData;
	}

	public String getFilenameData() {
		return filenameData;
	}

	public void setColumnMappings(
			PersonenImportColumnMapping[] mappingColumns) {
		this.mappingColumns = mappingColumns;
	}

	public PersonenImportColumnMapping[] getColumnMappings() {
		if (mappingColumns == null) {
			mappingColumns = new PersonenImportColumnMapping[0];
		}
		return mappingColumns;
	}

	public void setColumns(PersonenImportColumn[] columns) {
		this.columns = columns;
	}

	public PersonenImportColumn[] getColumns() {
		return columns;
	}

}
