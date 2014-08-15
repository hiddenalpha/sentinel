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
			ServerConfigurationDialog dialog = new ServerConfigurationDialog(null, info);
			dialog.setVisible(true);
		}



		/*if (!isServerSetupAlreadyDone) {
			// Dialog zu den Files ausserdem Passwörter
			FileImportConfigurationDialog dia = new FileImportConfigurationDialog(null);
			dia.setVisible(true);

			byte[] ausweisdaten = new byte[0];
			File fileAusweisDaten = dia.getPathAusweisdaten();
			String passwordAusweisDaten = null;
			if (fileAusweisDaten != null) {
				try {
					passwordAusweisDaten = dia.getPasswordAusweisdaten();
					ausweisdaten = Files.toByteArray(fileAusweisDaten);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			byte[] pisaDaten = new byte[0];
			File filePisadaten = dia.getPathPisaDaten();
			if (filePisadaten != null) {
				try {
					pisaDaten = Files.toByteArray(filePisadaten);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			byte[] konfigurationsdaten = new byte[0];
			File fileKonfigurationsDaten = dia.getPathKonfigurations();
			String passwordKonfigurationsDaten = null;
			if (fileKonfigurationsDaten != null) {
				try {
					log.debug("konfigurationsdaten gesetzt");
					passwordKonfigurationsDaten = dia.getPasswordKonfigurations();
					konfigurationsdaten = Files.toByteArray(fileKonfigurationsDaten);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Nun Ausweisdaten hart importieren
			if (fileAusweisDaten != null) {
				boolean result = ServiceHelper.getConfigurationsService().importPersonData(ausweisdaten, passwordAusweisDaten);
				if (result == false) {
					log.error("konnte ausweisdatne nicht importieren");
				}
			}

			// Nun Pisa-Daten importieren (Modification Views)
			if (filePisadaten != null) {
				//boolean result = ServiceHelper.getConfigurationsService().importPersonData(ausweisdaten, passwordAusweisDaten);
				//if (result == false) {
				//	log.error("konnte ausweisdatne nicht importieren");
				//}
			}

			// Konfigurationsdaten importieren
			if (passwordAusweisDaten == null) {
				passwordAusweisDaten = "";
			}
			if (passwordKonfigurationsDaten == null) {
				passwordKonfigurationsDaten = "";
			}

			log.debug("Server muss konfiguriert werden");
			InitialConfig config = ServiceHelper.getConfigurationsService().calculateInitialConfig(konfigurationsdaten, passwordKonfigurationsDaten, ausweisdaten, passwordAusweisDaten);
			ConfigValuesConfigurationDialog dialog = new ConfigValuesConfigurationDialog(null, "", config);
			dialog.setVisible(true);
		} else {
			log.debug("Server ist bereits konfiguriert");
		}*/
	}

}
