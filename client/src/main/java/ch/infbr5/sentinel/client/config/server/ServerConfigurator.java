package ch.infbr5.sentinel.client.config.server;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ServerSetupInformation;

public class ServerConfigurator {

	private static Logger log = Logger.getLogger(ServerConfigurator.class);

	public void configureServerConfiguration() {
		ServerSetupInformation info = ServiceHelper.getConfigurationsService().getServerSetupInformation();
		if (info.isServerIsConfigured()) {
			log.debug("Server ist konfiguriert");
		} else {
			log.debug("Server muss konfiguriert werden");
			ServerConfigurationDialog dialog = new ServerConfigurationDialog(null, info, true);
			dialog.setVisible(true);
		}
	}

}
