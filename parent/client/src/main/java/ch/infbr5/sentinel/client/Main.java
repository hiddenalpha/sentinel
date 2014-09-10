package ch.infbr5.sentinel.client;

import java.io.InputStream;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ch.infbr5.sentinel.client.gui.ApplicationFrame;

public class Main {

	private static Logger log = Logger.getLogger(Main.class);

	private static final String LOG4J_PROPERTIES_PRD = "/META-INF/log4j.properties";

	public static void main(String[] args) {

		// log4j
		InputStream inputStream = Main.class
				.getResourceAsStream(LOG4J_PROPERTIES_PRD);
		if (inputStream == null) {
			System.out.println("WARNING: Could not open configuration file");
			System.out.println("WARNING: Logging not configured");
		} else {
			PropertyConfigurator.configure(inputStream);
		}

		log.info("Starting Sentinel Client, Version "
				+ Version.get().getVersion() + " ("
				+ Version.get().getBuildTimestamp() + ")");

		// Shutdown Hook installieren
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Client ist beendet.");
			}
		});

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				new ApplicationFrame();
			}
		});
	}
}
