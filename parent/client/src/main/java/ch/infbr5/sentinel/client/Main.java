package ch.infbr5.sentinel.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ch.infbr5.sentinel.client.gui.ApplicationFrame;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Logger log = Logger.getLogger(Main.class.getName());
		log.setLevel(Level.ALL);
		log.info("initializing - trying to load configuration file ...");

		try {
			InputStream configFile = Main.class
					.getResourceAsStream("/META-INF/logging.properites");
			LogManager.getLogManager().readConfiguration(configFile);
		} catch (IOException ex) {
			System.out.println("WARNING: Could not open configuration file");
			System.out
					.println("WARNING: Logging not configured (console output only)");
		}
		log.info("starting Sentinel Client Version "
				+ Version.get().getVersion() + " ("
				+ Version.get().getBuildTimestamp() + ")");

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);

				new ApplicationFrame();
			}
		});
	}
}
