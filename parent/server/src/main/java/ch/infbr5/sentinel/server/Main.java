package ch.infbr5.sentinel.server;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ch.infbr5.sentinel.server.db.DatabaseMigration;
import ch.infbr5.sentinel.server.gui.ApplicationFrame;
import ch.infbr5.sentinel.server.logging.SystemMeldungAppender;

public class Main {

	private static ServerControl sentinelServer;

	private static Logger log = Logger.getLogger(Main.class);

	private static ApplicationFrame frame;

	private static final String LOG4J_PROPERTIES_DEV = "/META-INF/log4j.properties";

	private static String ipAddress;

	private static String port;

	private static boolean debugMode = false;

	private static boolean headless = false;

	public static void main(String[] args) {

		// 1. log4j konfigurieren
		InputStream inputStream = Main.class.getResourceAsStream(LOG4J_PROPERTIES_DEV);
		if (inputStream == null) {
			System.out.println("WARNING: Could not open configuration file");
			System.out.println("WARNING: Logging not configured");
		} else {
			PropertyConfigurator.configure(inputStream);
		}

		// 2. Datenbank migrieren
		DatabaseMigration migration = new DatabaseMigration();
		migration.start();

		// 3. Erst ab hier, darf aktiviert werden, dass Logs in die Datenbank geschrieben werden
		// Falls das vorher schon der Fall ist, wird der EntityManager erstellt, welcher dann einfach die Datenbank anpasst.
		SystemMeldungAppender.ENABLE = true;

		// Comand Line
		readCommandLine(args);

		// 3. UI Erstellen (bereits hier, das UI empfängt nun alle Logs)
		if (!headless) {
			frame = new ApplicationFrame();
		}

		// Java Info
		printJavaInfo();

		sentinelServer = new ServerControl(debugMode, false);

		// Shutdown Hook installieren
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Server wird gestoppt ...");
				sentinelServer.stop();
				log.info("Server wurde gestoppt");
			}
		});

		// Server starten
		log.debug("Server wird gestartet "+getConfigString()+" ...");
		sentinelServer.start(ipAddress, port);
		log.info(Version.getVersionDescription() + " " + getConfigString() + " gestartet");

		// Erst GUI anzeigen nachdem, der Server sauber gestartet wurde.
		if (!headless) {
			frame.show();
		}

		// warten bis Server beendet ist
		while (sentinelServer.isRunning()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}

		System.exit(0);
	}

	private static String getConfigString() {
		String conf = "(" + ipAddress + ":" + port;
		if (debugMode) {
			conf += ", debugMode";
		}
		if (headless) {
			conf += ", headless";
		}
		return conf + ")";
	}

	private static void readCommandLine(String[] args) {
		CommandLineReader reader = new CommandLineReader(args, ServerConfiguration.IP_ADDRESS, ServerConfiguration.PORT, ServerConfiguration.DEBUG_MODE, ServerConfiguration.HEADLESS);
		ipAddress = reader.getIp();
		port = reader.getPort();
		debugMode = reader.isDebugMode();
		headless = reader.isHeadless();
	}

	private static void printJavaInfo() {
		log.debug("Java-Hersteller: " + System.getProperty("java.vendor"));
		log.debug("Java-Version: " + System.getProperty("java.version"));
	}

}
