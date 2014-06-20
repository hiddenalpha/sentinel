package ch.infbr5.sentinel.server.ws.importer.mapping;

public class PersonenImportColumn {

	private int index;
	
	private String name;
	
	public PersonenImportColumn() {
		
	}
	
	public PersonenImportColumn(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
