package ch.infbr5.sentinel.client.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.infbr5.sentinel.client.util.NetworkUtil;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;

import com.sun.media.Log;

public class ConfigurationLocalHelper {

	private static final String FILE_DEFAULT_PROPERTIES = "/META-INF/default.properties";

	private static final String FILE_LAST_APPLICATION_PROPERTIES = "sentinel.properties";

	private static final String SERVER_HOSTNAME = "ServerHostname";

	private static final String FILE_CHOOSER_LAST_PATH = "FileChooserLastPath";

	private static final String SERVER_PORT_NUMBER = "ServerPortnumber";

	private static final String HOURS_INITIAL_LOAD_JOURNAL = "hoursInitialLoadJournal";

	private static final String CHECKPOINT_ID = "CheckpointId";

	private static final String ADMIN_MODE = "AdminMode";

	private static final String SUPERUSER_MODE = "SuperuserMode";

	private static ConfigurationLocalHelper config;

	public static ConfigurationLocalHelper getConfig() {
		if (config == null) {
			config = new ConfigurationLocalHelper();
		}
		return config;
	}

	public static boolean isFirstConfiguration() {
		File file = new File(FILE_LAST_APPLICATION_PROPERTIES);
		return !file.exists();
	}

	private Properties defaultProperties;

	private Properties applicatonProperties;

	private ConfigurationLocalHelper() {
		// Standard-Properties laden und diese als Applikationsproperties setzen
		try {
			defaultProperties = new Properties();
			InputStream in = this.getClass().getResourceAsStream(FILE_DEFAULT_PROPERTIES);
			defaultProperties.load(in);
			in.close();
			applicatonProperties = new Properties(defaultProperties);
		} catch (FileNotFoundException e) {
			Log.error(e);
		} catch (IOException e) {
			Log.error(e);
		}

		// Properties vom letzten Run nehmen und über die Standard-Properties schreiben
		if (!isFirstConfiguration()) {
			try {
				InputStream in = new FileInputStream(FILE_LAST_APPLICATION_PROPERTIES);
				applicatonProperties.load(in);
				in.close();
			} catch (FileNotFoundException e) {
				Log.error(e);
			} catch (IOException e) {
				Log.error(e);
			}
		}

		saveProperites();
	}

	private void saveProperites() {
		try {
			FileOutputStream out = new FileOutputStream(FILE_LAST_APPLICATION_PROPERTIES);
			applicatonProperties.store(out, "Last Application Properties");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String localImagePath = "";

	public String getServerHostname() {
		return getPropertyValue(SERVER_HOSTNAME);
	}

	public void setServerHostname(String host) {
		savePropertyValue(SERVER_HOSTNAME, host);
	}

	public String getFileChooserLastPath() {
		return getPropertyValue(FILE_CHOOSER_LAST_PATH);
	}

	public void setFileChooserLastPath(String path) {
		savePropertyValue(FILE_CHOOSER_LAST_PATH, path);
	}

	public String getServerPortnumber() {
		return getPropertyValue(SERVER_PORT_NUMBER);
	}

	public void setServerPortnumber(String port) {
		savePropertyValue(SERVER_PORT_NUMBER, port);
	}

	public String getEndpointAddress() {
		return "http://" + getServerHostname() + ":" + getServerPortnumber();
	}

	public int getHoursInitialLoadJournal() {
		String hours = getPropertyValue(HOURS_INITIAL_LOAD_JOURNAL);
		int h = 0;
		try {
			h = Integer.parseInt(hours);
		} catch (NumberFormatException e) {
			hours = defaultProperties.getProperty(HOURS_INITIAL_LOAD_JOURNAL);
			h = Integer.parseInt(hours);
		}
		return h;
	}

	public void setHoursInitialLoadJournal(int hours) {
		savePropertyValue(HOURS_INITIAL_LOAD_JOURNAL, String.valueOf(hours));
	}

	public Long getCheckpointId() {
		return Long.valueOf(getPropertyValue(CHECKPOINT_ID));
	}

	public void setCheckpointId(Long checkpointId) {
		savePropertyValue(CHECKPOINT_ID, String.valueOf(checkpointId));
	}

	public CheckpointDetails getCheckpoint() {
		CheckpointDetails details = new CheckpointDetails();
		details.setId(getCheckpointId());
		return details;
	}

	public CheckpointDetails getCheckpointWithName() {
		CheckpointDetails details = getCheckpoint();
		details.setName(ConfigurationHelper.getCheckpointName());
		return details;
	}

	public void setAdminMode(boolean mode) {
		savePropertyValue(ADMIN_MODE, Boolean.toString(mode));
	}

	public boolean isAdminMode() {
		String modeStr = getPropertyValue(ADMIN_MODE);
		return modeStr == null ? false : Boolean.parseBoolean(modeStr);
	}

	public void setSuperuserMode(boolean mode) {
		savePropertyValue(SUPERUSER_MODE, Boolean.toString(mode));
	}

	public boolean isSuperuserMode() {
		String modeStr = getPropertyValue(SUPERUSER_MODE);
		return modeStr == null ? false : Boolean.parseBoolean(modeStr);
	}

	public boolean isLocalMode() {
		return NetworkUtil.isLocalAdress(getServerHostname());
	}

	private boolean imagePathloaded = false;

	public String getLocalImagePath() {
		if (!imagePathloaded) {
			localImagePath = ServiceHelper.getConfigurationsService().getLocalImagePath();
			imagePathloaded = true;
		}
		return localImagePath;
	}

	private String getPropertyValue(String name) {
		return applicatonProperties.getProperty(name);
	}

	private void savePropertyValue(String name, String value) {
		applicatonProperties.setProperty(name, value);
		saveProperites();
	}

}
