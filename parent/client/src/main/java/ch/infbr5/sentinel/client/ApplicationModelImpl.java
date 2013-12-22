package ch.infbr5.sentinel.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ApplicationModelImpl implements ApplicationModel {
	private static String configFileName = "app.config";

	// operator name is not persisted
	private String operatorName;

	private SentinelConfiguration sentinelConfiguration;

	public ApplicationModelImpl() {
		try {
			this.loadConfiguration();
		} catch (IOException ex) {
			// file not found: create default config
			this.sentinelConfiguration = new SentinelConfiguration();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}



	public String getOperatorName() {
		return this.operatorName;
	}


	private void loadConfiguration() throws FileNotFoundException, IOException,
	ClassNotFoundException {
		FileInputStream fis = new FileInputStream(ApplicationModelImpl.configFileName);
		ObjectInputStream in = new ObjectInputStream(fis);
		this.sentinelConfiguration = (SentinelConfiguration) in.readObject();
		in.close();
	}

	public void reset() {
		this.setOperatorName("");
		this.sentinelConfiguration = new SentinelConfiguration();
	}

	public void saveConfiguration() {
		try {
			FileOutputStream outputStream = new FileOutputStream(ApplicationModelImpl.configFileName);
			ObjectOutputStream out = new ObjectOutputStream(outputStream);
			out.writeObject(this.sentinelConfiguration);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}


}
