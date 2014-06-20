package ch.infbr5.sentinel.client.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ConfigurationLocalHelper {

	private static ConfigurationLocalHelper config;

	public static ConfigurationLocalHelper getConfig() {
		if (config == null) {
			config = new ConfigurationLocalHelper();
		}
		return config;
	}

	private Properties applicationProps;
	private boolean localMode = false;
	private String localImagePath = "";

	private ConfigurationLocalHelper() {

		try {
			// create and load default properties
			Properties defaultProps = new Properties();
			InputStream in = this.getClass().getResourceAsStream(
					"/META-INF/default.properties");
			defaultProps.load(in);
			in.close();

			// create application properties with default
			applicationProps = new Properties(defaultProps);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// now load properties from last invocation
			InputStream in = new FileInputStream("sentinel.properties");
			applicationProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		saveProperites();

	}

	private void saveProperites() {
		try {
			FileOutputStream out;
			out = new FileOutputStream("sentinel.properties");
			applicationProps.store(out, "---No Comment---");
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getServerHostname() {
		return applicationProps.getProperty("ServerHostname");
	}

	public void setServerHostname(String host) {
		applicationProps.setProperty("ServerHostname", host);
		saveProperites();
	}
	
	public String getFileChooserLastPath() {
		return applicationProps.getProperty("FileChooserLastPath");
	}
	
	public void setFileChooserLastPath(String path) {
		applicationProps.setProperty("FileChooserLastPath", path);
		saveProperites();
	}

	public String getServerPortnumber() {
		return applicationProps.getProperty("ServerPortnumber");
	}

	public void setServerPortnumber(String port) {
		applicationProps.setProperty("ServerPortnumber", port);
		saveProperites();
	}

	public String getEndpointAddress() {
		return "http://" + getServerHostname() + ":" + getServerPortnumber();
	}

	public String getLocalEndpointAddress() {
		return "http://127.0.0.1:" + getServerPortnumber();
	}

	public boolean isLocalAdress(String ip) {
		try {
			InetAddress[] thisIp = InetAddress.getAllByName(InetAddress
					.getLocalHost().getHostName());
			for (int i = 0; i < thisIp.length; i++) {
				if (thisIp[i].getHostAddress().toString().equals(ip)) {
					return true;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public Long getCheckpointId() {
		return Long.valueOf(applicationProps.getProperty("CheckpointId"));
	}

	public void setCheckpointId(Long checkpointId) {
		applicationProps.setProperty("CheckpointId",
				String.valueOf(checkpointId));
		saveProperites();
	}

	public void setAdminMode(boolean mode) {
		applicationProps.setProperty("AdminMode", Boolean.toString(mode));
		saveProperites();
	}

	public boolean isAdminMode() {
		String modeStr = applicationProps.getProperty("AdminMode");
		if (modeStr != null) {
			return Boolean.parseBoolean(modeStr);
		} else {
			return false;
		}

	}

	public void setSuperuserMode(boolean mode) {
		applicationProps.setProperty("SuperuserMode", Boolean.toString(mode));
		saveProperites();
	}

	public boolean isSuperuserMode() {
		String modeStr = applicationProps.getProperty("SuperuserMode");
		if (modeStr != null) {
			return Boolean.parseBoolean(modeStr);
		} else {
			return false;
		}

	}

	public boolean isLocalMode() {
		return localMode;
	}

	public void setLocalMode(boolean localMode) {
		this.localMode = localMode;
	}

	public String getLocalImagePath() {
		return localImagePath;
	}

	public void setLocalImagePath(String localImagePath) {
		this.localImagePath = localImagePath;
	}

}
