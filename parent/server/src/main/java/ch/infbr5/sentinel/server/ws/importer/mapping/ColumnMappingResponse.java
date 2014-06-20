package ch.infbr5.sentinel.server.ws.importer.mapping;

public class ColumnMappingResponse {

	private PersonenImportColumn[] columns;
	
	private PersonenImportColumnMapping[] mappings;

	public PersonenImportColumn[] getColumns() {
		return columns;
	}

	public void setColumns(PersonenImportColumn[] columns) {
		this.columns = columns;
	}

	public PersonenImportColumnMapping[] getMappings() {
		return mappings;
	}

	public void setMappings(PersonenImportColumnMapping[] mappings) {
		this.mappings = mappings;
	}
	
}
