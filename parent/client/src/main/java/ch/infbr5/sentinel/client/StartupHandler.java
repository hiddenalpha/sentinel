package ch.infbr5.sentinel.client;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurator;
import ch.infbr5.sentinel.client.config.server.ServerConnectionConfigurator;

public class StartupHandler {

	public void startConfig() {
		boolean isFirstConfiguration = ConfigurationLocalHelper.isFirstConfiguration();

		ServerConnectionConfigurator srvConfig = new ServerConnectionConfigurator(isFirstConfiguration, true);
		srvConfig.configureServerConfiguration();

		CheckpointConfigurator ckpConfig = new CheckpointConfigurator(isFirstConfiguration, true);
		ckpConfig.configureCheckpointConfiguration();
	}

}
