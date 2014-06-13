package ch.infbr5.sentinel.server.ws.importer;

public class MappingPersonenAttributeToColumn {

	private PersonenAttribute personenAttribute;
	
	private Column column;
	
	public MappingPersonenAttributeToColumn() {
		
	}
	
	public MappingPersonenAttributeToColumn(PersonenAttribute personenAttribute, Column column) {
		this.personenAttribute = personenAttribute;
		this.column = column;
	}
	
	public PersonenAttribute getPersonenAttribute() {
		return personenAttribute;
	}
	
	public void setPersonenAttribute(PersonenAttribute personenAttribute) {
		this.personenAttribute = personenAttribute;
	}
	
	public Column getColumn() {
		return column;
	}
	
	public void setColumn(Column column) {
		this.column = column;
	}
	
}
