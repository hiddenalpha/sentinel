package ch.infbr5.sentinel.server.ws.admin;

import ch.infbr5.sentinel.server.ws.CheckpointDetails;
import ch.infbr5.sentinel.server.ws.ConfigurationDetails;
import ch.infbr5.sentinel.server.ws.EinheitDetails;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.PrintJobDetails;
import ch.infbr5.sentinel.server.ws.ZoneDetails;

public class ConfigurationResponse {

	private CheckpointDetails[] checkpointDetails;
	private ZoneDetails[] zoneDetails;
	private ConfigurationDetails[] configurationDetails;
	private PersonDetails[] personDetails;
	private EinheitDetails[] einheitDetails;
	private PrintJobDetails[] printJobDetails;

	public ConfigurationDetails[] getConfigurationDetails() {
		return configurationDetails;
	}

	public void setConfigurationDetails(ConfigurationDetails[] configurationDetails) {
		this.configurationDetails = configurationDetails;
	}

	public CheckpointDetails[] getCheckpointDetails() {
		return this.checkpointDetails;
	}

	public ZoneDetails[] getZoneDetails() {
		return this.zoneDetails;
	}

	public void setCheckpointDetails(CheckpointDetails[] checkpointDetails) {
		this.checkpointDetails = checkpointDetails;
	}

	public void setZoneDetails(ZoneDetails[] zoneDetails) {
		this.zoneDetails = zoneDetails;
	}

	public void setPersonDetails(PersonDetails[] personDetails) {
		this.personDetails = personDetails;
	}
	
	public PersonDetails[] getPersonDetails() {
		return personDetails;
	}

	public void setEinheitDetails(EinheitDetails[] einheitDetails) {
		this.einheitDetails = einheitDetails;
	}
	
	public EinheitDetails[] getEinheitDetails() {
		return einheitDetails;
	}
	
	public void setPrintJobDetails(PrintJobDetails[] printJobDetails) {
		this.printJobDetails = printJobDetails;
	}
	
	public PrintJobDetails[] getPrintJobDetails() {
		return printJobDetails;
	}
}
