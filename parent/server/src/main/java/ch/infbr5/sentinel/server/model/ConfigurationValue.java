package ch.infbr5.sentinel.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = "findConfigurationValueByKey", query = "SELECT c FROM ConfigurationValue c WHERE c.key like :keyParam"),
		@NamedQuery(name = "getConfigurationValueById", query = "SELECT c FROM ConfigurationValue c WHERE c.id = :idParam"), 
		@NamedQuery(name = "getConfigurationValues", query = "SELECT c FROM ConfigurationValue c")})
public class ConfigurationValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String key;
	private String stringValue;
	private long longValue;
	private String validFor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getValidFor() {
		return validFor;
	}

	public void setValidFor(String validFor) {
		this.validFor = validFor;
	}

}
