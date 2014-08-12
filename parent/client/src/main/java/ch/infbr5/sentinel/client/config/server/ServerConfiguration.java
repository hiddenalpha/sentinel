package ch.infbr5.sentinel.client.config.server;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class ServerConfiguration {

	private static Logger log = Logger.getLogger(ServerConfiguration.class);

	private boolean isFirstConfiguration;

	public ServerConfiguration(boolean isFirstConfiguration) {
		this.isFirstConfiguration = isFirstConfiguration;
	}

	public void configureServerConfiguration() {
		boolean success = false;

		if (isFirstConfiguration) {
			askForServerConfiguration("Dies ist eine Erstkonfiguration. Überprüfen Sie lediglich die Einstellungen. Im Normalfall muss hier keine Anpassung vorgenommen werden.");
			isFirstConfiguration = false;
		}

		while (!success) {
			if (configureEndpointAddress()) {
				if (isServerReachable()) {
					success = true;
				}
			}
			if (!success) {
				askForServerConfiguration("Es konnte keine Verbindung zum eingetragenen Server aufgebaut werden. Möglicherweise läuft der Server nicht oder die Verbindungsdaten sind falsch.");
			}
		}
	}

	/**
	 * Konfiguriert die Services. Setzt die Endpoint-Adresse. Falls eine
	 * Exception auftritt, sind die EndpointAdressen ungültig.
	 *
	 * @return true, falls die Konfiguration erfolgreich war, anderenfalls
	 *         false.
	 */
	private boolean configureEndpointAddress() {
		String endpointAddress = ConfigurationLocalHelper.getConfig().getEndpointAddress();
		try {
			ServiceHelper.setEndpointAddress(endpointAddress);
			log.debug("Endpoint-Addresse konnte konfiguriert werden: " + endpointAddress);
			return true;
		} catch (Exception e) {
			log.debug("Endpoint-Addresse konnte nicht konfiguriert werden: " + endpointAddress);
			log.error(e);
			return false;
		}
	}

	/**
	 * Prüft ob die Servers erreichbar sind.
	 *
	 * @return true, falls ja, anderenfalls false.
	 */
	private boolean isServerReachable() {
		boolean isReachable = false;
		try {
			String ping = ServiceHelper.getSentinelService().ping();
			if ("pong".equals(ping)) {
				log.debug("Client konnte sich erfolgreich mit dem Server verbinden");
				isReachable = true;
			}
		} catch (Exception e) {
			log.error(e);
		}
		if (!isReachable) {
			log.debug("Client konnte sich nicht mit dem Server verbinden");
		}
		return isReachable;
	}

	/**
	 * Frägt den Client nach der Serververbindung.
	 */
	private void askForServerConfiguration(String info) {
		log.debug("Client nach Serverkonfiguration fragen.");
		ServerConfigurationDialog dialog = new ServerConfigurationDialog(null, this, info, ConfigurationLocalHelper.getConfig().getServerHostname(), ConfigurationLocalHelper.getConfig().getServerPortnumber());
		dialog.setVisible(true);
	}

}
