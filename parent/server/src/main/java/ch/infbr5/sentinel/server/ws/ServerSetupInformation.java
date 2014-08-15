package ch.infbr5.sentinel.server.ws;

import java.util.List;

import com.google.common.collect.Lists;

public class ServerSetupInformation {

	private boolean serverIsConfigured;

	private boolean checkpointConfigured;

	private String checkpointName;

	private boolean zoneConfigured;

	private String zonenName;

	private String adminPassword;

	private String superUserPassword;

	private String identityCardPassword;

	private List<String> ipCamUrls = Lists.newArrayList();

	public boolean isCheckpointConfigured() {
		return checkpointConfigured;
	}

	public void setCheckpointConfigured(boolean checkpointConfigured) {
		this.checkpointConfigured = checkpointConfigured;
	}

	public String getCheckpointName() {
		return checkpointName;
	}

	public void setCheckpointName(String checkpointName) {
		this.checkpointName = checkpointName;
	}

	public boolean isZoneConfigured() {
		return zoneConfigured;
	}

	public void setZoneConfigured(boolean zoneConfigured) {
		this.zoneConfigured = zoneConfigured;
	}

	public String getZonenName() {
		return zonenName;
	}

	public void setZonenName(String zonenName) {
		this.zonenName = zonenName;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getSuperUserPassword() {
		return superUserPassword;
	}

	public void setSuperUserPassword(String superUserPassword) {
		this.superUserPassword = superUserPassword;
	}

	public String getIdentityCardPassword() {
		return identityCardPassword;
	}

	public void setIdentityCardPassword(String identityCardPassword) {
		this.identityCardPassword = identityCardPassword;
	}

	public boolean isServerIsConfigured() {
		return serverIsConfigured;
	}

	public void setServerIsConfigured(boolean serverIsConfigured) {
		this.serverIsConfigured = serverIsConfigured;
	}

	public void calculateServerSetup() {
		boolean configured = true;
		if (!checkpointConfigured) {
			configured = false;
		}
		if (!zoneConfigured) {
			configured = false;
		}
		if (adminPassword == null || adminPassword.isEmpty()) {
			configured = false;
		}
		if (superUserPassword == null || superUserPassword.isEmpty()) {
			configured = false;
		}
		if (identityCardPassword == null || identityCardPassword.isEmpty()) {
			configured = false;
		}
		this.serverIsConfigured = configured;
	}

	public List<String> getIpCamUrls() {
		return ipCamUrls;
	}

	public void setIpCamUrls(List<String> ipCamUrls) {
		this.ipCamUrls = ipCamUrls;
	}


}
