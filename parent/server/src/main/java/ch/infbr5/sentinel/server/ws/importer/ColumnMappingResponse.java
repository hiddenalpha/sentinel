package ch.infbr5.sentinel.server.ws.importer;

public class ColumnMappingResponse {

	private Column[] columns;
	
	private MappingPersonenAttributeToColumn[] mappings;

	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public MappingPersonenAttributeToColumn[] getMappings() {
		return mappings;
	}

	public void setMappings(MappingPersonenAttributeToColumn[] mappings) {
		this.mappings = mappings;
	}
	
}
