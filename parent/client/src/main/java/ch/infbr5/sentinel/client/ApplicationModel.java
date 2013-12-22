package ch.infbr5.sentinel.client;

public interface ApplicationModel {

	public String getOperatorName();

	public void reset();

	public void saveConfiguration();

	public void setOperatorName(String operatorName);
}
