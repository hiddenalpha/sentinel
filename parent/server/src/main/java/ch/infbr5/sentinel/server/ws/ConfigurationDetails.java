package ch.infbr5.sentinel.server.ws;

public class ConfigurationDetails {

	private Long id;
	private String key;
	private String stringValue;
	private Long longValue;
	private String validFor;

	public String getValidFor() {
		return validFor;
	}

	public void setValidFor(String validFor) {
		this.validFor = validFor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

}
