package ch.infbr5.sentinel.client.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;

public class ConfigurationHelper {

	private static final Logger log = Logger.getLogger(ConfigurationHelper.class);

	public static List<ConfigurationDetails> loadConfigurationIPCams() {
		ConfigurationResponse response = ServiceHelper.getConfigurationsService().getGlobalConfigurationValues("URL_IPCAM_%");
		return response.getConfigurationDetails();
	}

	public static URL[] getIPCams() {
		List<ConfigurationDetails> details = loadConfigurationIPCams();
		List<URL> urls = new ArrayList<URL>();
		for (ConfigurationDetails detail : details) {
			try {
				URL camURL = new URL(detail.getStringValue());
				urls.add(camURL);
			} catch (MalformedURLException e) {
				log.warn("Keine gültige URL für IP-Cam: " + detail.getStringValue());
			}
		}
		return urls.toArray(new URL[0]);
	}

	public static String getCheckpointName() {
		ConfigurationResponse response = ServiceHelper.getConfigurationsService().getCheckpoints();
		List<CheckpointDetails> res = response.getCheckpointDetails();
		for (Iterator<CheckpointDetails> iterator = res.iterator(); iterator.hasNext();) {
			CheckpointDetails cd = iterator.next();
			if (cd.getId() == ConfigurationLocalHelper.getConfig().getCheckpointId()) {
				return cd.getName();
			}
		}
		return ""; // TODO Darf nie eintreten, müsste sofort exceptoin geben
	}

}
