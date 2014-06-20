package ch.infbr5.sentinel.server.ws.importer.mapping;

public class PersonenImportColumnMapping {

	private PersonenAttribute personenAttribute;
	
	private PersonenImportColumn column;
	
	private PersonenImportColumn[] possibleColumns;
	
	public PersonenImportColumnMapping() {
		
	}
	
	public PersonenImportColumnMapping(PersonenAttribute personenAttribute, PersonenImportColumn column) {
		this.personenAttribute = personenAttribute;
		this.column = column;
	}
	
	public PersonenAttribute getPersonenAttribute() {
		return personenAttribute;
	}
	
	public void setPersonenAttribute(PersonenAttribute personenAttribute) {
		this.personenAttribute = personenAttribute;
	}
	
	public PersonenImportColumn getColumn() {
		return column;
	}
	
	public void setColumn(PersonenImportColumn column) {
		this.column = column;
	}

	public PersonenImportColumn[] getPossibleColumns() {
		return possibleColumns;
	}

	public void setPossibleColumns(PersonenImportColumn[] possibleColumns) {
		this.possibleColumns = possibleColumns;
	}
	
}
