package ch.infbr5.sentinel.client.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;

public class ConfigurationHelper {

	public static URL[] getIPCams() {

		List<URL> tmp = new ArrayList<URL>();

		ConfigurationResponse response = ServiceHelper.getConfigurationsService().getConfigurationValue(ConfigurationLocalHelper.getConfig().getCheckpointId(), "URL_IPCAM_%");
		List<ConfigurationDetails> liste = response.getConfigurationDetails();

		for (Iterator<ConfigurationDetails> iterator = liste.iterator(); iterator.hasNext();) {
			ConfigurationDetails config = iterator.next();

			try {
				URL camURL = new URL(config.getStringValue());
				tmp.add(camURL);
			} catch (MalformedURLException e) {
				// If its no URL so let it be.
			}
		}

		return tmp.toArray(new URL[0]);
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
		return "";
	}

}
