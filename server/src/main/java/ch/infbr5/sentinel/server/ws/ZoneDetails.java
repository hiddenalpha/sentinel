package ch.infbr5.sentinel.server.ws;

public class ZoneDetails {
	private Long id;
	private String name;
	private boolean undOpRegeln;

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public boolean isUndOpRegeln() {
		return this.undOpRegeln;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUndOpRegeln(boolean undOpRegeln) {
		this.undOpRegeln = undOpRegeln;
	}
}
