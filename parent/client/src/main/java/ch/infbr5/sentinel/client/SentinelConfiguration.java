package ch.infbr5.sentinel.client;

import java.io.Serializable;

public class SentinelConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String serverName;

	private Long checkpointId;

	private String adminPassword;

	public String getAdminPassword() {
		return this.adminPassword;
	}

	public Long getCheckpointId() {
		return this.checkpointId;
	}

	public String getServerName() {
		return this.serverName;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public void setCheckpointId(Long checkpointId) {
		this.checkpointId = checkpointId;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
