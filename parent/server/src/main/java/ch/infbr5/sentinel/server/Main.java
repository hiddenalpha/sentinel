package ch.infbr5.sentinel.server;

import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ch.infbr5.sentinel.server.gui.ApplicationFrame;

public class Main {

	private static ServerControl sentinelServer;

	private static Logger log = Logger.getLogger(Main.class);

	private static final String LOG4J_PROPERTIES = "/META-INF/log4j.properties";

	private static boolean debugMode = false;

	public static void main(String[] args) {

		// log4j
		InputStream inputStream = Main.class.getResourceAsStream(LOG4J_PROPERTIES);
		if (inputStream == null) {
			System.out.println("WARNING: Could not open configuration file");
			System.out.println("WARNING: Logging not configured");
		} else {
			PropertyConfigurator.configure(inputStream);
		}

		// Starting UI
		ApplicationFrame frame = new ApplicationFrame();
		frame.show();

		log.debug("Server startet: Java-Vendor:" + System.getProperty("java.vendor") + " Java-Version:" + System.getProperty("java.version"));

		// cli
		Options options = new Options();
		Option ipAddress = OptionBuilder.withArgName("ip").hasArg().withDescription("Server IpAdress").create("ipAddress");
		Option debug = new Option("debug", "print debugging information");
		options.addOption(ipAddress);
		options.addOption(debug);

		// create the parser
		CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("debug")) {
				debugMode = true;
			}

			String ip = line.getOptionValue("ipAddress", "0.0.0.0");
			log.debug("listening on ".concat(ip));

			sentinelServer = new ServerControl(debugMode, false);

			// Shutdown Hook installieren
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					log.info("trying to stop server ...");
					sentinelServer.stop();
					log.info("server stopped.");
				}
			});

			// Server starten
			log.debug("trying to start server ...");
			sentinelServer.start(ip);
			// Erst ab hier auf Info Level logen.
			log.info("Sentinel server version " + Version.get().getVersion()
					+ " (" + Version.get().getBuildTimestamp()
					+ ") is running.");

			// warten bis Server beendet ist
			while (sentinelServer.isRunning()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.error(e);
				}
			}

			System.exit(0);

		} catch (ParseException exp) {
			log.warn("Parsing failed.  Reason: " + exp.getMessage());
		}

	}
}
