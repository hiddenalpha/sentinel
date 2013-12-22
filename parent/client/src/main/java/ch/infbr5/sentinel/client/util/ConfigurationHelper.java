package ch.infbr5.sentinel.client.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;

public class ConfigurationHelper {

	public static URL[] getIPCams() {

		List<URL> tmp = new ArrayList<URL>();

		ConfigurationResponse response = ServiceHelper.getConfigurationsService().getConfigurationValue(1, "URL_IPCAM_%");
		List<ConfigurationDetails> liste = response.getConfigurationDetails();

		for (Iterator<ConfigurationDetails> iterator = liste.iterator(); iterator.hasNext();) {
			ConfigurationDetails config = iterator.next();

			try {
				URL camURL = new URL(config.getStringValue());
				tmp.add(camURL);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tmp.toArray(new URL[0]);
	}

	public static String getPassword(String user) {
		ConfigurationResponse response = ServiceHelper.getConfigurationsService().getGlobalConfigurationValue("PASSWORD_" + user);

		if (response.getConfigurationDetails().size() > 0) {
			ConfigurationDetails config = response.getConfigurationDetails().get(0);
			return config.getStringValue();
		}
		return null;
	}

	public static void setPassword(String user, String password) {
		ConfigurationDetails config = new ConfigurationDetails();
		config.setKey("PASSWORD_" + user);
		config.setStringValue(password);
		config.setValidFor("");
		ServiceHelper.getConfigurationsService().updateConfigurationValue(config);
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
