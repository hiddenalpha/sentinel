package ch.infbr5.sentinel.server.importer.personen.state;

import ch.infbr5.sentinel.server.ws.importer.Column;
import ch.infbr5.sentinel.server.ws.importer.MappingPersonenAttributeToColumn;

public class PersonenImporterState {

	private String filenameData;

	private Column[] columns;

	private MappingPersonenAttributeToColumn[] mappingColumns;

	private boolean isKompletterBestand;
	
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

	public void setMappingColumns(
			MappingPersonenAttributeToColumn[] mappingColumns) {
		this.mappingColumns = mappingColumns;
	}

	public MappingPersonenAttributeToColumn[] getMappingColumns() {
		return mappingColumns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public Column[] getColumns() {
		return columns;
	}

}
