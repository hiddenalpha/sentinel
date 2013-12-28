package ch.infbr5.sentinel.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;

public class Main {

	private static ServerControl sentinelServer;
	private static Logger log;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		log = Logger.getLogger(Main.class.getName());
		log.setLevel(Level.ALL);
		log.info(System.getProperty("java.vendor")+ " " + System.getProperty("java.version"));
		log.info("initializing - trying to load configuration file ...");
		try {
			InputStream configFile = Main.class.getResourceAsStream("/META-INF/logging.properites");
			LogManager.getLogManager().readConfiguration(configFile);
		} catch (IOException ex) {
			System.out.println("WARNING: Could not open configuration file");
			System.out.println("WARNING: Logging not configured (console output only)");
		}
		log.info("trying to start server ...");

		boolean debugMode = false;

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
			log.info("listening on ".concat(ip));

			
			sentinelServer = new ServerControl(debugMode,false);

			// Shutdown Hook installieren
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					log.info("trying to stop server ...");
					sentinelServer.stop();
					log.info("server stopped.");
				}
			});

			// Server starten
			log.info("trying to start server ...");
			sentinelServer.start(ip);
			log.info("server is running.");
			
			// warten bis Server beendet ist
			while (sentinelServer.isRunning()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.exit(0);

		} catch (ParseException exp) {
			// oops, something went wrong
			log.severe("Parsing failed.  Reason: " + exp.getMessage());
		}

	}
}
